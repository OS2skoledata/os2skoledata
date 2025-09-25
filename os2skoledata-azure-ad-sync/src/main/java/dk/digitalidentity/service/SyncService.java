package dk.digitalidentity.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.microsoft.graph.models.User;
import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
import dk.digitalidentity.config.modules.UsernameStandard;
import dk.digitalidentity.service.model.DBGroup;
import dk.digitalidentity.service.model.DBUser;
import dk.digitalidentity.service.model.Institution;
import dk.digitalidentity.service.model.ModificationHistory;
import dk.digitalidentity.service.model.OS2skoledataSchemaDTO;
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
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SyncService {

	@Autowired
	private AzureADService azureADService;

	@Autowired
	private OS2skoledataService os2skoledataService;

	@Autowired
	private OS2skoledataAzureADConfiguration config;

	private final char[] first = "23456789abcdefghjkmnpqrstuvxyz".toCharArray();
	private final char[] second = "abcdefghjkmnpqrstuvxyz".toCharArray();
	private final char[] third = "23456789".toCharArray();
	private Map<Integer, String> uniqueIds = new HashMap<>();
	private List<String> globalLockedInstitutionNumbers = new ArrayList<>();
	private Random random = new Random();

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

			// ensure custom schema exists
			String schemaId = os2skoledataService.getSchemaId();
			schemaId = azureADService.ensureOS2SkoledataSchemaExists(schemaId);

			// all users
			List<User> allUsers = azureADService.getAllUsers(schemaId);

			// all usernames
			List<User> allUsersManagedByOS2skoledata = azureADService.getUsersManagedByOS2skoledata(schemaId);
			List<String> allUsernames = azureADService.getAllUsernames();
			if (allUsernames == null) {
				throw new Exception("Failed to get all usernames from AD. Will not perform sync.");
			}

			List<String> allUsernamesOS2skoledata = os2skoledataService.getAllUsernames();
			Map<String, List<String>> usernameMap = azureADService.generateUsernameMap(allUsernames, allUsernamesOS2skoledata);
			if (usernameMap == null) {
				throw new Exception("Failed to generate username map from AD. Will not perform sync.");
			}

			// delete users fully after x days if enabled
			azureADService.deleteUsers(allUsersManagedByOS2skoledata, schemaId);

			// institutions
			List<Institution> institutions = os2skoledataService.getInstitutions();
			List<String> lockedInstitutionNumbers = institutions.stream().filter(Institution::isLocked).map(Institution::getInstitutionNumber).collect(Collectors.toList());

			// add locked institutions to global list
			// institutions that are no longer locked will be removed from the list when the full sync is done (to make sure first sync of unlocked institutions is a full sync)
			globalLockedInstitutionNumbers.addAll(lockedInstitutionNumbers);

			// pr institution update users
			Map<String, List<DBUser>> institutionUserMap = new HashMap<>();
			List<DBUser> allDBUsers = new ArrayList<>();
			for (Institution institution : institutions) {
				if (institution.isLocked()) {
					continue;
				}
				List<DBUser> users = os2skoledataService.getUsersForInstitution(institution);
				institutionUserMap.put(institution.getInstitutionNumber(), users);
				allDBUsers.addAll(users);
			}

			// fetch locked usernames to make sure they are not disabled - then check if others should be deleted
			List<String> lockedUsernames = os2skoledataService.getLockedUsernames();
			List<String> keepAliveUsernames = os2skoledataService.getKeepAliveUsernames();
			azureADService.disableInactiveUsers(allDBUsers, allUsersManagedByOS2skoledata, lockedUsernames, keepAliveUsernames, schemaId);

			List<String> securityGroupIds = new ArrayList<>();
			List<String> securityGroupIdsForRenamedGroups = new ArrayList<>();
			List<String> teamIds = new ArrayList<>();
			List<String> teamIdsForRenamedTeams = new ArrayList<>();
			List<DBUser> allDBUsersForGlobalGroups = new ArrayList<>();
			for (Institution institution : institutions) {
				if (institution.isLocked()) {
					log.info("Not updating users for institution " + institution.getInstitutionNumber() +  ". Institution is locked due to school year change.");
					continue;
				}

				List<String> excludedRoles = getExcludedRoles(institution.getInstitutionNumber());
				List<DBUser> users = institutionUserMap.get(institution.getInstitutionNumber());

				for (DBUser user : users) {
					log.info("Checking if user with databaseId " + user.getDatabaseId() + " should be created or updated");
					User match;
					if (config.getSyncSettings().isUseUsernameAsKey()) {
						match = allUsers.stream().filter(u -> u.mailNickname != null && u.mailNickname.equals(user.getUsername())).findAny().orElse(null);
					} else {
						match = allUsers.stream().filter(u -> u.employeeId != null && u.employeeId.equals(user.getCpr())).findAny().orElse(null);
					}

					if (match == null) {
						// should user be created?
						boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
						if (shouldBeExcluded) {
							log.info("Not creating user with databaseId " + user.getDatabaseId() + " because it has an excluded role");
							continue;
						}

						// find available username and create
						String username = null;
						if (user.getReservedUsername() != null && allUsers.stream().noneMatch(u -> u.mailNickname != null && u.mailNickname.equals(user.getReservedUsername()))) {
							// we can't add this username to the usernameMap as we do not know the form of it - the municipality create the reservedUsername
							username = user.getReservedUsername();
						} else if (user.getStilUsername() != null && (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN_RANDOM)))
						{
							boolean exists = allUsers.stream().anyMatch(u -> u.mailNickname != null && u.mailNickname.equals(user.getStilUsername()));
							if (!exists)
							{
								username = user.getStilUsername();
								addUsernameToMap(user.getStilUsername(), usernameMap);
							} else
							{
								username = generateUsername(user.getFirstName(), usernameMap);
							}
						} else
						{
							username = generateUsername(user.getFirstName(), usernameMap);
						}

						if (username == null) {
							throw new Exception("Failed to generate username for user with database id " + user.getDatabaseId());
						}

						user.setUsername(username);
						os2skoledataService.setUsernameOnUser(user.getDatabaseId(), username);
						log.info("Generated username " + username + " for user with databaseId " + user.getDatabaseId());
						User createdUser = azureADService.createAccount(username, user, schemaId);

						// is null if userDryRun
						if (createdUser != null) {
							allUsersManagedByOS2skoledata.add(createdUser);
							allUsers.add(createdUser);
							log.info("Created Account for user with databaseId " + user.getDatabaseId() + " and username " + username + ".");
						}
					}
					else {
						String username = null;
						if (match.userPrincipalName != null) {
							username = match.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "");
						}

						if (username == null) {
							throw new Exception("UserPrincipalName / username on user was null. Something is wrong. Azure user with id " + match.id);
						}

						// delete if the user is managed by OS2skoledata and should be excluded
						boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
						if (shouldBeExcluded) {
							if (match.companyName != null && match.companyName.equals("OS2skoledata")) {
								azureADService.disableUser(match.id, username, schemaId);
							}
						} else {
							user.setAzureId(match.id);

							// update username in OS2skoledata
							if (user.getUsername() == null || !username.equals(user.getUsername())) {
								user.setUsername(username);
								os2skoledataService.setUsernameOnUser(user.getDatabaseId(), username);
							}

							azureADService.updateAccount(user, match, schemaId);
						}
					}
				}

				// security groups for institution
				List<DBGroup> classes = os2skoledataService.getClassesForInstitution(institution);
				azureADService.updateSecurityGroups(institution, users, classes, securityGroupIds, securityGroupIdsForRenamedGroups);

				// teams for institution
				azureADService.updateTeams(institution, users, classes, teamIds, teamIdsForRenamedTeams, allDBUsers);

				allDBUsersForGlobalGroups.addAll(users);
			}

			handleGlobalGroupsAndCleanUp(allDBUsersForGlobalGroups, securityGroupIds, lockedUsernames, securityGroupIdsForRenamedGroups, teamIds, teamIdsForRenamedTeams);

			setHeadOnInstitutions(institutions, head);

			// full sync was a success - update the locked institution list to only have the current locked institutions
			globalLockedInstitutionNumbers = lockedInstitutionNumbers;

			log.info("Finished executing fullsyncjob");
		}
		catch (Exception e) {
			log.error("Failed to perform full sync. Exception happened: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private void setHeadOnInstitutions(List<Institution> institutions, long head) throws Exception {
		// set head on not locked institutions
		for (Institution institution : institutions) {
			if (!institution.isLocked()) {
				os2skoledataService.setHead(head, institution.getInstitutionNumber());
			}
		}
	}

	private void handleGlobalGroupsAndCleanUp(List<DBUser> allDBUsersForGlobalGroups, List<String> securityGroupIds, List<String> lockedUsernames, List<String> securityGroupIdsForRenamedGroups, List<String> teamIds, List<String> teamIdsForRenamedTeams) throws Exception {
		// global security groups
		azureADService.updateGlobalSecurityGroups(allDBUsersForGlobalGroups, securityGroupIds, lockedUsernames);

		// fetch group ids from locked institutions to make sure they are not deleted and delete groups that are not needed anymore
		List<String> lockedGroupIds = os2skoledataService.getLockedGroupIds();
		azureADService.deleteNotNeededGroups(securityGroupIds, lockedGroupIds, securityGroupIdsForRenamedGroups);

		// fetch team ids from locked institutions to make sure they are not deleted and delete teams that are not needed anymore
		List<String> lockedTeamIds = os2skoledataService.getLockedTeamIds();
		azureADService.archiveAndDeleteNotNeededTeams(teamIds, lockedTeamIds, teamIdsForRenamedTeams);
	}

	public void deltaSync() {
		try {
			List<Institution> institutions = os2skoledataService.getInstitutions();
			for (Institution institution : institutions) {
				if (institution.isLocked() || globalLockedInstitutionNumbers.contains(institution.getInstitutionNumber()))
				{
					log.info("Not performing delta sync on institution with number " + institution.getInstitutionNumber() + ". Institution is locked.");
					if (institution.isLocked() && !globalLockedInstitutionNumbers.contains(institution.getInstitutionNumber()))
					{
						globalLockedInstitutionNumbers.add(institution.getInstitutionNumber());
					}
					continue;
				}

				List<ModificationHistory> changes = os2skoledataService.getChanges(institution.getInstitutionNumber());
				if (!changes.isEmpty()) {

					// init
					azureADService.initializeClient();

					// ensure custom schema exists
					String schemaId = os2skoledataService.getSchemaId();
					schemaId = azureADService.ensureOS2SkoledataSchemaExists(schemaId);

					// all usernames
					List<User> allUsers = azureADService.getAllUsers(schemaId);
					List<String> allUsernames = azureADService.getAllUsernames();
					if (allUsernames == null) {
						throw new Exception("Failed to get all usernames from AD. Will not perform sync.");
					}

					List<String> allUsernamesOS2skoledata = os2skoledataService.getAllUsernames();
					Map<String, List<String>> usernameMap = azureADService.generateUsernameMap(allUsernames, allUsernamesOS2skoledata);
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
					handlePersonChanges(typePerson, changedUsers, usernameMap, allUsers, schemaId);

					// setting head last
					os2skoledataService.setHead(changes.get(0).getId(), institution.getInstitutionNumber());
				}
			}
		}
		catch (Exception e) {
			if (e.getMessage().equals("Do a full sync")) {
				log.info("Too many changes for delta sync. Doing a full sync instead.");
				fullSync();
			}
			else {
				log.error("Failed to execute DeltaSyncJob", e);
			}
		}
	}

	private void handlePersonChanges(List<ModificationHistory> typePerson, List<DBUser> changedUsers, Map<String, List<String>> usernameMap, List<User> allUsers, String schemaId) throws Exception {
		List<String> keepAliveUsernames = os2skoledataService.getKeepAliveUsernames();
		for (DBUser user : changedUsers) {
			if (user.getInstitutions().stream().anyMatch(i -> i.isLocked() || globalLockedInstitutionNumbers.contains(i.getInstitutionNumber()))) {
				log.info("Skipping deltasync for user with db id " + user.getDatabaseId() + ". User is in at least one locked institution.");
				continue;
			}

			List<ModificationHistory> userChanges = typePerson.stream().filter(c -> c.getEntityId() == user.getDatabaseId()).toList();
			boolean create = userChanges.stream().anyMatch(c -> c.getEventType().equals(EventType.CREATE));
			boolean update = userChanges.stream().anyMatch(c -> c.getEventType().equals(EventType.UPDATE));
			boolean delete = userChanges.stream().anyMatch(c -> c.getEventType().equals(EventType.DELETE));

			log.info("Delta sync handling user with databaseId " + user.getDatabaseId());
			User match;
			if (config.getSyncSettings().isUseUsernameAsKey()) {
				match = allUsers.stream().filter(u -> u.mailNickname != null && u.mailNickname.equals(user.getUsername())).findAny().orElse(null);
			} else {
				match = allUsers.stream().filter(u -> u.employeeId != null && u.employeeId.equals(user.getCpr())).findAny().orElse(null);
			}

			if (!user.isDeleted() && create && match == null) {
				// should user be created?
				List<String> excludedRoles = getExcludedRoles(user.getCurrentInstitutionNumber());
				boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
				if (shouldBeExcluded) {
					continue;
				}

				// find available username and create
				String username = null;
				if (user.getReservedUsername() != null && allUsers.stream().noneMatch(u -> u.mailNickname != null && u.mailNickname.equals(user.getReservedUsername()))) {
					// we can't add this username to the usernameMap as we do not know the form of it - the municipality create the reservedUsername
					username = user.getReservedUsername();
				} else if (user.getStilUsername() != null && (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN_RANDOM)))
				{
					boolean exists = allUsers.stream().anyMatch(u -> u.mailNickname != null && u.mailNickname.equals(user.getStilUsername()));
					if (!exists)
					{
						username = user.getStilUsername();
						addUsernameToMap(user.getStilUsername(), usernameMap);
					} else
					{
						username = generateUsername(user.getFirstName(), usernameMap);
					}
				} else
				{
					username = generateUsername(user.getFirstName(), usernameMap);
				}

				if (username == null) {
					throw new Exception("Failed to generate username for user with databaseId " + user.getDatabaseId());
				}

				user.setUsername(username);
				os2skoledataService.setUsernameOnUser(user.getDatabaseId(), username);
				log.info("Delta sync generated username " + username + " for user with databaseId " + user.getDatabaseId());
				User createdUser = azureADService.createAccount(username, user, schemaId);

				// is null if userDryRun
				if (createdUser != null) {
					allUsers.add(createdUser);
					log.info("Delta sync created account for user with databaseId " + user.getDatabaseId() + " and username" + username);
				}
			}
			else if (!user.isDeleted() && create && match != null) {
				update = true;
			}

			if (!user.isDeleted() && update && match != null) {
				String username = null;
				if (match.userPrincipalName != null) {
					username = match.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "");
				}

				if (username == null) {
					throw new Exception("UserPrincipalName / username on user was null. Something is wrong. Azure user with id " + match.id);
				}

				// delete if the user should be excluded and is managed by OS2skoledata
				List<String> excludedRoles = getExcludedRoles(user.getCurrentInstitutionNumber());
				boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
				if (shouldBeExcluded) {
					if (match.companyName != null && match.companyName.equals("OS2skoledata")) {
						azureADService.disableUser(match.id, username, schemaId);
					}
				} else {
					// update username in OS2skoledata
					if (user.getUsername() == null || !username.equals(user.getUsername())) {
						user.setUsername(username);
						os2skoledataService.setUsernameOnUser(user.getDatabaseId(), username);
					}

					azureADService.updateAccount(user, match, schemaId);
				}
			}

			if (delete && match != null) {
				if (match.companyName != null && match.companyName.equals("OS2skoledata")) {
					boolean allowDisabling = true;
					if (keepAliveUsernames != null && keepAliveUsernames.contains(match.mailNickname)) {
						allowDisabling = false;
					}

					if (allowDisabling) {
						azureADService.disableUser(match.id, match.mailNickname, schemaId);
					}
				}
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
		if (user.getGlobalRole().equals(Role.STUDENT)) {
			if (excludedRoles.contains(user.getGlobalStudentRole().toString())) {
				return true;
			}
		}
		else if (user.getGlobalRole().equals(Role.EMPLOYEE)) {
			for (EmployeeRole role : user.getGlobalEmployeeRoles()) {
				if (!excludedRoles.contains(role.toString())) {
					return false;
				}
			}
			return true;
		}
		else if (user.getGlobalRole().equals(Role.EXTERNAL)) {
			if (excludedRoles.contains(user.getGlobalExternalRole().toString())) {
				return true;
			}
		}
		else {
			log.info("Unknown role " + user.getRole() + " for user with username " + user.getUsername() + ". Disabling user / not creating user.");
			return true;
		}

		return false;
	}

	private void addUsernameToMap(String stilUsername, Map<String, List<String>> usernameMap) {
		String key = stilUsername.substring(0,4);
		if (!usernameMap.containsKey(key)) {
			usernameMap.put(key, new ArrayList<>());
		}
		usernameMap.get(key).add(stilUsername);
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
			String username;
			if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.RANDOM)) {
				username = namePart + generateRandomNumber(config.getSyncSettings().getUsernameSettings().getRandomStandardNumberCount());
			} else {
				username = prefix + ((nameFirst) ? namePart : uniqueIds.get(i)) + ((nameFirst) ? uniqueIds.get(i) : namePart);
			}

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

	public int generateRandomNumber(int length) {
		int min = (int) Math.pow(10, length - 1);
		int max = (int) Math.pow(10, length) - 1;
		return random.nextInt((max - min) + 1) + min;
	}

	private String getNamePart(String firstname) {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.RANDOM)) {
			char[] possibleChars = "abcdefghjkmnpqrstuvwxyz".toCharArray();
			StringBuilder randomCharsBuilder = new StringBuilder(config.getSyncSettings().getUsernameSettings().getRandomStandardLetterCount());

			// Generate random characters from possibleChars array
			for (int i = 0; i < config.getSyncSettings().getUsernameSettings().getRandomStandardLetterCount(); i++) {
				int randomIndex = random.nextInt(possibleChars.length);
				randomCharsBuilder.append(possibleChars[randomIndex]);
			}

			return randomCharsBuilder.toString();
		} else {
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
	}

	private int getNamePartLength() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN)) {
			return 4;
		}
		else if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.RANDOM)) {
			return config.getSyncSettings().getUsernameSettings().getRandomStandardLetterCount();
		}
		return 3;
	}

	private String getPrefix() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.RANDOM)) {
			return "";
		}
		return config.getSyncSettings().getUsernameSettings().getUsernamePrefix() == null ? "" : config.getSyncSettings().getUsernameSettings().getUsernamePrefix();
	}

	private boolean isNameFirst() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.PREFIX_NAME_LAST) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.THREE_NUMBERS_THREE_CHARS_FROM_NAME)) {
			return false;
		}
		return true;
	}

	private void populateTable() {
		if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN)) {
			int idx = 0;
			char[] possibleNumbers = "0123456789".toCharArray();
			for (int i = 0; i < possibleNumbers.length; i++) {
				for (int j = 0; j < possibleNumbers.length; j++) {
					for (int k = 0; k < possibleNumbers.length; k++) {
						for (int l = 0; l < possibleNumbers.length; l++) {
							uniqueIds.put(idx++, ("" + possibleNumbers[i] + possibleNumbers[j] + possibleNumbers[k] + possibleNumbers[l]));
						}
					}
				}
			}
		} else if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN_RANDOM) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.RANDOM)) {
			// do nothing, random number will be generated on request
		} else if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.THREE_NUMBERS_THREE_CHARS_FROM_NAME)) {
			int idx = 0;
			char[] possibleNumbers = "23456789".toCharArray();
			for (int i = 0; i < possibleNumbers.length; i++) {
				for (int j = 0; j < possibleNumbers.length; j++) {
					for (int k = 0; k < possibleNumbers.length; k++) {
						uniqueIds.put(idx++, ("" + possibleNumbers[i] + possibleNumbers[j] + possibleNumbers[k]));
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

	public void teamsAndGroupsSync() {
		log.info("Executing teams and groups sync");
		try {

			// head
			long head = os2skoledataService.getHead();

			// init
			azureADService.initializeClient();

			// all users
			List<User> allUsers = azureADService.getAllUsers(null);

			// institutions
			List<Institution> institutions = os2skoledataService.getInstitutions();

			// pr institution update users
			Map<String, List<DBUser>> institutionUserMap = new HashMap<>();
			List<DBUser> allDBUsers = new ArrayList<>();
			for (Institution institution : institutions) {
				if (institution.isLocked()) {
					continue;
				}
				List<DBUser> users = os2skoledataService.getUsersForInstitution(institution);
				institutionUserMap.put(institution.getInstitutionNumber(), users);
				allDBUsers.addAll(users);
			}

			List<String> securityGroupIds = new ArrayList<>();
			List<String> securityGroupIdsForRenamedGroups = new ArrayList<>();
			List<String> teamIds = new ArrayList<>();
			List<String> teamIdsForRenamedTeams = new ArrayList<>();
			List<DBUser> allDBUsersForGlobalGroups = new ArrayList<>();

			// handle users first to allow cross institution team admins
			for (Institution institution : institutions) {
				if (institution.isLocked()) {
					log.info("Not updating teams and groups for institution " + institution.getInstitutionNumber() +  ". Institution is locked due to school year change.");
					continue;
				}

				List<String> excludedRoles = getExcludedRoles(institution.getInstitutionNumber());
				List<DBUser> users = institutionUserMap.get(institution.getInstitutionNumber());
				for (DBUser user : users) {
					if (user.getUsername() == null || user.getUsername().isEmpty()) {
						continue;
					}

					User match;
					if (config.getSyncSettings().isUseUsernameAsKey()) {
						match = allUsers.stream().filter(u -> u.mailNickname != null && u.mailNickname.equals(user.getUsername())).findAny().orElse(null);
					} else {
						match = allUsers.stream().filter(u -> u.employeeId != null && u.employeeId.equals(user.getCpr())).findAny().orElse(null);
					}

					if (match == null) {
						log.debug("Not handling user with username " + user.getUsername() + " because it was not found in azure AD");
						continue;
					}

					boolean shouldBeExcluded = shouldBeExcluded(user, excludedRoles);
					if (shouldBeExcluded) {
						log.info("Not handling user with username " + user.getUsername() + " because it has an excluded role");
						continue;
					}

					user.setAzureId(match.id);
				}
			}

			for (Institution institution : institutions) {
				if (institution.isLocked()) {
					log.info("Not updating teams and groups for institution " + institution.getInstitutionNumber() +  ". Institution is locked due to school year change.");
					continue;
				}

				List<DBUser> users = institutionUserMap.get(institution.getInstitutionNumber());

				// security groups for institution
				List<DBGroup> classes = os2skoledataService.getClassesForInstitution(institution);
				azureADService.updateSecurityGroups(institution, users, classes, securityGroupIds, securityGroupIdsForRenamedGroups);

				// teams for institution
				azureADService.updateTeams(institution, users, classes, teamIds, teamIdsForRenamedTeams, allDBUsers);

				allDBUsersForGlobalGroups.addAll(users);
			}

			// fetch locked usernames to make sure they are not removed from global groups
			List<String> lockedUsernames = os2skoledataService.getLockedUsernames();

			// global security groups
			handleGlobalGroupsAndCleanUp(allDBUsersForGlobalGroups, securityGroupIds, lockedUsernames, securityGroupIdsForRenamedGroups, teamIds, teamIdsForRenamedTeams);

			// set head on not locked institutions
			setHeadOnInstitutions(institutions, head);

			log.info("Finished executing teams and groups sync");
		}
		catch (Exception e) {
			log.error("Failed to perform teams and groups sync. Exception happened: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
