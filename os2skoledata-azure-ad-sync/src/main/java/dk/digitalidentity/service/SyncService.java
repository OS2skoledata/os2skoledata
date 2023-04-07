package dk.digitalidentity.service;

import com.microsoft.graph.models.User;
import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
import dk.digitalidentity.config.modules.UsernameStandard;
import dk.digitalidentity.service.model.DBGroup;
import dk.digitalidentity.service.model.Institution;
import dk.digitalidentity.service.model.DBUser;
import dk.digitalidentity.service.model.ModificationHistory;
import dk.digitalidentity.service.model.enums.EmployeeRole;
import dk.digitalidentity.service.model.enums.EntityType;
import dk.digitalidentity.service.model.enums.EventType;
import dk.digitalidentity.service.model.enums.Role;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class SyncService {

	@Autowired
	private AzureADService azureADService;

	@Autowired
	private OS2skoledataService os2skoledataService;

	@Autowired
	private OS2skoledataAzureADConfiguration config;

	private final char[] first = "0123456789abcdefghjklmnpqrstuvxyz".toCharArray();
	private final char[] second = "abcdefghjklmnpqrstuvxyz".toCharArray();
	private final char[] third = "0123456789".toCharArray();
	private Map<Integer, String> uniqueIds = new HashMap<>();

	@PostConstruct
	public void init() {
		populateTable();
	}

	public void fullSync() {
		log.info("Executing fullSync");
		try {

			// head
			long head = os2skoledataService.getHead();

			// init
			azureADService.initializeClient();

			// all users
			List<User> allUsers = azureADService.getAllUsers();

			// all usernames
			List<User> allUsersManagedByOS2skoledata = azureADService.getUsersManagedByOS2skoledata();
			List<String> allUsernames = azureADService.getAllUsernames();
			if (allUsernames == null) {
				throw new Exception("Failed to get all usernames from AD. Will not perform sync.");
			}
			Map<String, List<String>> usernameMap = azureADService.generateUsernameMap(allUsernames);
			if (usernameMap == null) {
				throw new Exception("Failed to generate username map from AD. Will not perform sync.");
			}

			// institutions
			List<Institution> institutions = os2skoledataService.getInstitutions();

			// pr institution update users
			Map<String, List<DBUser>> institutionUserMap = new HashMap<>();
			List<DBUser> allDBUsers = new ArrayList<>();
			for (Institution institution : institutions) {
				List<DBUser> users = os2skoledataService.getUsersForInstitution(institution);
				institutionUserMap.put(institution.getInstitutionNumber(), users);
				allDBUsers.addAll(users);
			}

			// delete users
			azureADService.disableInactiveUsers(allDBUsers, allUsersManagedByOS2skoledata);

			List<String> securityGroupIds = new ArrayList<>();
			List<DBUser> allDBUsersForGlobalGroups = new ArrayList<>();
			for (Institution institution : institutions) {
				List<String> excludedRoles = getExcludedRoles(institution.getInstitutionNumber());
				List<DBUser> users = institutionUserMap.get(institution.getInstitutionNumber());

				for (DBUser user : users) {
					log.info("Checking if user with databaseId " + user.getDatabaseId() + " should be created or updated");
					User match = allUsers.stream().filter(u -> u.employeeId != null && u.employeeId.equals(user.getCpr())).findAny().orElse(null);

					if (match == null) {
						// should user be created?
						boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
						if (shouldBeExcluded) {
							log.info("Not creating user with databaseId " + user.getDatabaseId() + " because it has an excluded role");
							continue;
						}

						// find available username and create
						String username = generateUsername(user.getFirstName(), usernameMap);
						if (username == null) {
							throw new Exception("Failed to generate username for user with LocalPersonId " + user.getLocalPersonId());
						}

						user.setUsername(username);
						os2skoledataService.setUsernameOnUser(user.getLocalPersonId(), username);
						log.info("Generated username " + username + " for user with databaseId " + user.getDatabaseId());
						User createdUser = azureADService.createAccount(username, user);
						allUsersManagedByOS2skoledata.add(createdUser);
						allUsers.add(createdUser);
						log.info("Created Account for user with databaseId " + user.getDatabaseId() + " and username " + username + ".");
					}
					else {
						user.setAzureId(match.id);

						String username = null;
						if (match.userPrincipalName != null) {
							username = match.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "");
						}

						if (username == null) {
							throw new Exception("UserPrincipalName / username on user was null. Something is wrong. Azure user with id " + match.id);
						}

						// delete if the user should be excluded
						boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
						if (shouldBeExcluded) {
							azureADService.disableUser(match.id, username);
						}

						// update username in OS2skoledata
						if (user.getUsername() == null || !username.equals(user.getUsername())) {
							user.setUsername(username);
							os2skoledataService.setUsernameOnUser(user.getLocalPersonId(), username);
						}

						azureADService.updateAccount(user, match);
					}
				}

				// security groups for institution
				List<DBGroup> classes = os2skoledataService.getClassesForInstitution(institution);
				azureADService.updateSecurityGroups(institution, users, classes, securityGroupIds);

				allDBUsersForGlobalGroups.addAll(users);
			}

			// global security groups
			azureADService.updateGlobalSecurityGroups(allDBUsersForGlobalGroups, securityGroupIds);

			// delete groups that are not needed anymore
			azureADService.deleteNotNeededGroups(securityGroupIds);

			// set head
			os2skoledataService.setHead(head);
		}
		catch (Exception e) {
			os2skoledataService.reportError(e.getMessage());
			log.error("Failed to perform full sync. Exception happened: " + e.getMessage());
		}

	}

	public void deltaSync() {
		try {
			List<ModificationHistory> changes = os2skoledataService.getChanges();
			if (!changes.isEmpty()) {

				// init
				azureADService.initializeClient();

				// all usernames
				List<User> allUsers = azureADService.getAllUsers();
				List<String> allUsernames = azureADService.getAllUsernames();
				if (allUsernames == null) {
					throw new Exception("Failed to get all usernames from AD. Will not perform sync.");
				}
				Map<String, List<String>> usernameMap = azureADService.generateUsernameMap(allUsernames);
				if (usernameMap == null) {
					throw new Exception("Failed to generate username map from AD. Will not perform sync.");
				}

				changes.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));

				// we only handle person changes right now. We are not using ous.
				List<ModificationHistory> typePerson = changes.stream().filter(c -> c.getEntityType().equals(EntityType.INSTITUTION_PERSON)).toList();
				//				List<ModificationHistory> typeInstitution = changes.stream().filter(c -> c.getEntityType().equals(EntityType.INSTITUTION)).toList();
				//				List<ModificationHistory> typeGroup = changes.stream().filter(c -> c.getEntityType().equals(EntityType.GROUP)).toList();

				List<Long> changedPersonIds = typePerson.stream().map(ModificationHistory::getEntityId).toList();
				//				List<Long> changedInstitutionIds = typeInstitution.stream().map(ModificationHistory::getEntityId).toList();
				//				List<Long> changedGroupIds = typeGroup.stream().map(ModificationHistory::getEntityId).toList();

				List<DBUser> changedUsers = os2skoledataService.getChangedUsers(changedPersonIds);
				//				List<Institution> changedInstitutions = os2skoledataService.getChangedInstitutions(changedInstitutionIds);
				//				List<DBGroup> changedGroups = os2skoledataService.getChangedGroups(changedGroupIds);

				// handle changes
				//				handleInstitutionChanges(typeInstitution, changedInstitutions);
				//				handleGroupChanges(typeGroup, changedGroups);
				handlePersonChanges(typePerson, changedUsers, usernameMap, allUsers);

				// setting head last
				os2skoledataService.setHead(changes.get(0).getId());
			}
		}
		catch (Exception e) {
			if (e.getMessage().equals("Do a full sync")) {
				log.info("Too many changes for delta sync. Doing a full sync instead.");
				fullSync();
			}
			else {
				os2skoledataService.reportError(e.getMessage());
				log.error("Failed to execute DeltaSyncJob", e);
			}
		}
	}

	private void handlePersonChanges(List<ModificationHistory> typePerson, List<DBUser> changedUsers, Map<String, List<String>> usernameMap, List<User> allUsers) throws Exception {
		for (DBUser user : changedUsers) {
			List<ModificationHistory> userChanges = typePerson.stream().filter(c -> c.getEntityId() == user.getDatabaseId()).toList();
			boolean create = userChanges.stream().anyMatch(c -> c.getEventType().equals(EventType.CREATE));
			boolean update = userChanges.stream().anyMatch(c -> c.getEventType().equals(EventType.UPDATE));
			boolean delete = userChanges.stream().anyMatch(c -> c.getEventType().equals(EventType.DELETE));

			log.info("Delta sync handling user with databaseId " + user.getDatabaseId());
			User match = allUsers.stream().filter(u -> u.employeeId != null && u.employeeId.equals(user.getCpr())).findAny().orElse(null);
			if (create && match == null) {
				// should user be created?
				List<String> excludedRoles = getExcludedRoles(user.getCurrentInstitutionNumber());
				boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
				if (shouldBeExcluded) {
					continue;
				}

				// find available username and create
				String username = generateUsername(user.getFirstName(), usernameMap);
				if (username == null) {
					throw new Exception("Failed to generate username for user with LocalPersonId " + user.getLocalPersonId());
				}

				user.setUsername(username);
				os2skoledataService.setUsernameOnUser(username, user.getLocalPersonId());
				log.info("Delta sync generated username " + username + " for user with databaseId " + user.getDatabaseId());
				User createdUser = azureADService.createAccount(username, user);
				allUsers.add(createdUser);
				log.info("Delta sync created account for user with databaseId " + user.getDatabaseId() + " and username" + username);
			}
			else if (create && match != null) {
				update = true;
			}

			if (update && match != null) {
				String username = null;
				if (match.userPrincipalName != null) {
					username = match.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "");
				}

				if (username == null) {
					throw new Exception("UserPrincipalName / username on user was null. Something is wrong. Azure user with id " + match.id);
				}

				// delete if the user should be excluded
				List<String> excludedRoles = getExcludedRoles(user.getCurrentInstitutionNumber());
				boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
				if (shouldBeExcluded) {
					azureADService.disableUser(match.id, "" + user.getDatabaseId());
				}

				// update username in OS2skoledata
				if (user.getUsername() == null || !username.equals(user.getUsername())) {
					user.setUsername(username);
					os2skoledataService.setUsernameOnUser(username, user.getLocalPersonId());
				}

				azureADService.updateAccount(user, match);
			}

			if (delete && match != null) {
				azureADService.disableUser(match.id, "" + user.getDatabaseId());
			}
		}
	}

	//	private void handleGroupChanges(List<ModificationHistory> typeGroup, List<DBGroup> changedGroups) {
	//	}
	//
	//	private void handleInstitutionChanges(List<ModificationHistory> typeInstitution, List<Institution> changedInstitutions) {
	//	}

	private List<String> getExcludedRoles(String institutionNumber) {
		Set<String> result = new HashSet<>();
		if (config.getSyncSettings().getFilteringSettings().getGloballyExcludedRoles() != null) {
			result.addAll(config.getSyncSettings().getFilteringSettings().getGloballyExcludedRoles());
		}

		if (config.getSyncSettings().getFilteringSettings().getExludedRolesInInstitution() != null) {
			if (config.getSyncSettings().getFilteringSettings().getExludedRolesInInstitution().containsKey(institutionNumber)) {
				result.addAll(config.getSyncSettings().getFilteringSettings().getExludedRolesInInstitution().get(institutionNumber));
			}
		}
		return new ArrayList<>(result);
	}

	private boolean shouldBeExcluded(DBUser user, List<String> excludedRoles) {
		if (user.getRole().equals(Role.STUDENT)) {
			if (excludedRoles.contains(user.getStudentRole().toString())) {
				return true;
			}
		}
		else if (user.getRole().equals(Role.EMPLOYEE)) {
			for (EmployeeRole role : user.getEmployeeRoles()) {
				if (!excludedRoles.contains(role.toString())) {
					return false;
				}
			}
			return true;
		}
		else if (user.getRole().equals(Role.EXTERNAL)) {
			if (excludedRoles.contains(user.getExternalRole().toString())) {
				return true;
			}
		}
		else {
			log.info("Unknow role " + user.getRole() + " for user with username " + user.getUsername() + ". Disabling user / not creating user.");
			return true;
		}

		return false;
	}

	private String generateUsername(String firstName, Map<String, List<String>> usernameMap) throws Exception {
		String namePart = getNamePart(firstName);
		String prefix = getPrefix();
		boolean nameFirst = isNameFirst();
		boolean mapContainsKey = usernameMap.containsKey(namePart);
		List<String> usernamesWithNamePart = new ArrayList<>();
		if (mapContainsKey) {
			usernamesWithNamePart = usernameMap.get(namePart);
		}

		// 1000 tries is crazy many
		for (int i = 0; i < 1000; i++) {
			String username = prefix + ((nameFirst) ? namePart : uniqueIds.get(i)) + ((nameFirst) ? uniqueIds.get(i) : namePart);

			if (!usernamesWithNamePart.contains(username)) {
				// can be optimized if necessary. Can do a stream anymatch on all users/usernames but it won't be a real time check
				if (!azureADService.accountExists(username)) {
					if (!mapContainsKey) {
						usernameMap.put(namePart, new ArrayList<>());
					}

					usernameMap.get(namePart).add(username);

					return username;
				}
			}
		}

		return null;
	}

	private String getNamePart(String firstname) {
		int namePartLength = getNamePartLength();
		String name = firstname.toLowerCase()
				.replace("æ", "ae")
				.replace("ø", "oe")
				.replace("å", "aa");

		name = name.replaceAll("[^a-zA-Z\\d]*", "");

		if (name.length() >= namePartLength) {
			return name.substring(0, namePartLength).toLowerCase();
		}
		else {
			while (name.length() < namePartLength) {
				name = name + "x";
			}
		}

		return name;
	}

	private int getNamePartLength() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN)) {
			return 4;
		}
		return 3;
	}

	private String getPrefix() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN)) {
			return "";
		}
		return config.getSyncSettings().getUsernameSettings().getUsernamePrefix() == null ? "" : config.getSyncSettings().getUsernameSettings().getUsernamePrefix();
	}

	private boolean isNameFirst() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.PREFIX_NAME_LAST)) {
			return false;
		}
		return true;
	}

	private void populateTable() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN)) {
			int idx = 0;

			for (int i = 0; i < third.length; i++) {
				for (int j = 0; j < third.length; j++) {
					for (int k = 0; k < third.length; k++) {
						for (int l = 0; l < third.length; l++) {
							uniqueIds.put(idx++, ("" + third[i] + third[j] + third[k] + third[l]));
						}
					}
				}
			}
		}
		else {
			int idx = 0;

			for (int i = 0; i < third.length; i++) {
				for (int j = 0; j < second.length; j++) {
					for (int k = 0; k < first.length; k++) {
						uniqueIds.put(idx++, ("" + third[i] + second[j] + first[k]));
					}
				}
			}
		}
	}
}
