package dk.digitalidentity.service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.AadUserConversationMember;
import com.microsoft.graph.models.ConversationMember;
import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.EducationClass;
import com.microsoft.graph.models.EducationUser;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.Team;
import com.microsoft.graph.models.TeamArchiveParameterSet;
import com.microsoft.graph.models.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.ConversationMemberCollectionPage;
import com.microsoft.graph.requests.ConversationMemberCollectionRequestBuilder;
import com.microsoft.graph.requests.EducationUserCollectionWithReferencesPage;
import com.microsoft.graph.requests.EducationUserCollectionWithReferencesRequestBuilder;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.GroupCollectionRequestBuilder;
import com.microsoft.graph.requests.TeamCollectionPage;
import com.microsoft.graph.requests.TeamCollectionRequestBuilder;
import com.microsoft.graph.requests.UserCollectionPage;
import com.microsoft.graph.requests.UserCollectionRequestBuilder;
import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
import dk.digitalidentity.config.modules.UsernameStandard;
import dk.digitalidentity.service.model.DBGroup;
import dk.digitalidentity.service.model.DBUser;
import dk.digitalidentity.service.model.Institution;
import dk.digitalidentity.service.model.enums.EntityType;
import dk.digitalidentity.service.model.enums.Role;
import dk.digitalidentity.service.model.enums.SetFieldType;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AzureADService {

	@Autowired
	private OS2skoledataAzureADConfiguration config;

	@Autowired
	private OS2skoledataService os2skoledataService;

	private ClientSecretCredential clientSecretCredential;
	private GraphServiceClient<Request> appClient;

	public void initializeClient() {
		if (clientSecretCredential == null) {
			clientSecretCredential = new ClientSecretCredentialBuilder()
					.clientId(config.getAzureAd().getClientID())
					.clientSecret(config.getAzureAd().getClientSecret())
					.tenantId(config.getAzureAd().getTenantID())
					.build();
		}

		if (appClient == null) {
			List<String> scopes = new ArrayList<>();
			scopes.add("https://graph.microsoft.com/.default");
			final TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(scopes, clientSecretCredential);

			appClient = GraphServiceClient.builder()
					.authenticationProvider(authProvider)
					.buildClient();
		}
	}

	public Map<String, List<String>> generateUsernameMap(List<String> allUsernamesAzure, List<String> allOS2skoledataUsernames) throws Exception {
		Map<String, List<String>> map = new HashMap<>();
		Set<String> allUsernames = new HashSet<>(allUsernamesAzure);
		allUsernames.addAll(allOS2skoledataUsernames);
		for (String username : allUsernames) {
			String key = "";
			if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.FROM_STIL_OR_AS_UNILOGIN)) {
				try {
					if (username.length() != 8) {
						continue;
					}
					key = username.substring(0, 4);
				}
				catch (Exception e) {
					log.warn("Failed to add username " + username + " to username map", e);
					continue;
				}
			}
			else if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.PREFIX_NAME_FIRST)) {
				try {
					String usernameWithoutPrefix = username.replace(config.getSyncSettings().getUsernameSettings().getUsernamePrefix(), "");
					if (usernameWithoutPrefix.length() < 3) {
						continue;
					}
					key = usernameWithoutPrefix.substring(0, 3);
				}
				catch (Exception e) {
					log.warn("Failed to add username " + username + " to username map", e);
					continue;
				}
			}
			else if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.PREFIX_NAME_LAST) || config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.THREE_NUMBERS_THREE_CHARS_FROM_NAME)) {
				try {
					String usernameWithoutPrefix = username.replace(config.getSyncSettings().getUsernameSettings().getUsernamePrefix(), "");
					if (usernameWithoutPrefix.length() < 3) {
						continue;
					}
					key = username.substring(username.length() - 3);
				}
				catch (Exception e) {
					log.warn("Failed to add username " + username + " to username map", e);
					continue;
				}
			}
			else {
				throw new Exception("Failed to generate username map. Unknown usernameStandardType: " + config.getSyncSettings().getUsernameSettings().getUsernameStandard().toString());
			}

			if (!map.containsKey(key)) {
				map.put(key, new ArrayList<>());
			}

			map.get(key).add(username);
		}

		log.info("Generated username map");

		return map;
	}

	// not all user fields are returned, expand to get more fields
	public List<User> getUsersManagedByOS2skoledata() throws Exception {
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
		requestOptions.add(new QueryOption("$count", "true"));

		// the companyName eq 'OS2skoledata'" marks that the user is managed by OS2skoledata
		UserCollectionPage userCollection = appClient.users()
				.buildRequest(requestOptions)
				.select("id,givenName,surname,displayName,userPrincipalName,employeeId,companyName,accountEnabled,mailNickname,department")
				.filter("companyName eq 'OS2skoledata'")
				.get();

		// returns 100 users pr page
		List<User> result = new ArrayList<>();
		while (userCollection != null) {
			result.addAll(userCollection.getCurrentPage());
			final UserCollectionRequestBuilder nextPage = userCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				userCollection = nextPage.buildRequest().get();
			}
		}

		return result;
	}

	// not all user fields are returned, expand to get more fields
	public List<User> getAllUsers() throws Exception {
		LinkedList<Option> requestOptions = new LinkedList<Option>();
		requestOptions.add(new HeaderOption("ConsistencyLevel", "eventual"));
		requestOptions.add(new QueryOption("$count", "true"));

		UserCollectionPage userCollection = appClient.users()
				.buildRequest(requestOptions)
				.select("id,givenName,surname,displayName,userPrincipalName,employeeId,companyName,accountEnabled,mailNickname,mail,department")
				.get();

		// returns 100 users pr page
		List<User> result = new ArrayList<>();
		while (userCollection != null) {
			result.addAll(userCollection.getCurrentPage());
			final UserCollectionRequestBuilder nextPage = userCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				userCollection = nextPage.buildRequest().get();
			}
		}

		return result;
	}

	public List<String> getAllUsernames() {
		UserCollectionPage userCollection = appClient.users()
				.buildRequest()
				.select("userPrincipalName")
				.get();
		List<User> result = new ArrayList<>();
		while (userCollection != null) {
			result.addAll(userCollection.getCurrentPage());
			final UserCollectionRequestBuilder nextPage = userCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				userCollection = nextPage.buildRequest().get();
			}
		}
		return result.stream().filter(u -> u.userPrincipalName != null).map(u -> u.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "")).toList();
	}

	public User getUser(String idOrEmail) {
		return appClient.users(idOrEmail).buildRequest().get();
	}

	private User createUser(User user) {
		return appClient.users().buildRequest().post(user);
	}

	public void disableUser(String id, String identification) throws Exception {
		if (config.getAzureAd().isUserDryRun()) {
			log.info("UserDryRun: Would have disabled user with username/databaseId " + identification);
			return;
		}

		User user = new User();
		user.accountEnabled = false;

		try {
			updateUser(user, id);
			log.info("Disabled user with username/databaseId " + identification);
		}
		catch (Exception e) {
			throw new Exception("Failed to disable user with username/databaseId " + identification, e);
		}
	}

	public void updateUser(User user, String id) {
		if (config.getAzureAd().isUserDryRun()) {
			log.info("UserDryRun: Would have updated user with username " + user.userPrincipalName);
			return;
		}

		appClient.users(id)
				.buildRequest()
				.patch(user);
	}

	public List<Group> getSecurityGroups() {

		// a security group has mailEnabled false, securityEnabled true and groupType empty
		GroupCollectionPage groupCollection = appClient.groups()
				.buildRequest()
				.filter("securityEnabled eq true and mailEnabled eq false")
				.get();

		List<Group> result = new ArrayList<>();
		while (groupCollection != null) {
			result.addAll(groupCollection.getCurrentPage());
			final GroupCollectionRequestBuilder nextPage = groupCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				groupCollection = nextPage.buildRequest().get();
			}
		}

		// can maybe be done in the query (filter) but I can't make it work
		return result.stream().filter(g -> g.groupTypes == null || g.groupTypes.isEmpty()).toList();
	}

	public Group createGroup(Group group) {
		return appClient.groups().buildRequest().post(group);
	}

	public EducationClass createEducationClass(EducationClass educationClass) {
		return appClient.education().classes().buildRequest().post(educationClass);
	}

	public Group getGroup(String id) {
		return appClient.groups(id).buildRequest().get();
	}

	public EducationClass getEducationClass(String id) {
		return appClient.education().classes(id).buildRequest().get();
	}

	public Group updateGroup(Group group, String id) {
		return appClient.groups(id).buildRequest().patch(group);
	}

	private void deleteGroup(String id, boolean forTeam, String name) {
		appClient.groups(id).buildRequest().delete();
		String andTeam = forTeam? " and team" : "";
		log.info("Deleted group" + andTeam + " with id " + id + " and name " + name );
	}

	public List<User> getUserMembersOfGroup(String id) {
		UserCollectionPage userCollection = appClient.groups(id).membersAsUser().buildRequest().get();

		// returns 100 users pr page
		List<User> result = new ArrayList<>();
		while (userCollection != null) {
			result.addAll(userCollection.getCurrentPage());
			final UserCollectionRequestBuilder nextPage = userCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				userCollection = nextPage.buildRequest().get();
			}
		}

		return result;
	}

	public List<User> getUserOwnersOfGroup(String id) {
		UserCollectionPage userCollection = appClient.groups(id).ownersAsUser().buildRequest().get();

		// returns 100 users pr page
		List<User> result = new ArrayList<>();
		while (userCollection != null) {
			result.addAll(userCollection.getCurrentPage());
			final UserCollectionRequestBuilder nextPage = userCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				userCollection = nextPage.buildRequest().get();
			}
		}

		return result;
	}

	public void addMemberToGroup(String groupId, String objectId) {
		DirectoryObject directoryObject = new DirectoryObject();
		directoryObject.id = objectId;

		appClient.groups(groupId).members().references()
				.buildRequest()
				.post(directoryObject);
	}

	public void addOwnerToGroup(String groupId, String objectId) {
		DirectoryObject directoryObject = new DirectoryObject();
		directoryObject.id = objectId;

		appClient.groups(groupId).owners().references()
				.buildRequest()
				.post(directoryObject);
	}

	public void removeMemberFromGroup(String groupId, String objectId) {
		appClient.groups(groupId).members(objectId).reference().buildRequest().delete();
	}

	public void removeOwnerFromGroup(String groupId, String objectId) {
		appClient.groups(groupId).owners(objectId).reference().buildRequest().delete();
	}

	public void disableInactiveUsers(List<DBUser> dbUsers, List<User> azureOS2skoledataUsers, List<String> lockedUsernames) throws Exception {
		for (User azureUser : azureOS2skoledataUsers) {
			if (azureUser.mailNickname != null) {

				// if user is in locked institution, skip
				if (lockedUsernames != null && lockedUsernames.contains(azureUser.mailNickname)) {
					continue;
				}

				boolean exists;
				if (config.getSyncSettings().isUseUsernameAsKey()) {
					exists = dbUsers.stream().anyMatch(u -> Objects.equals(u.getUsername(), azureUser.mailNickname));
				} else {
					exists = dbUsers.stream().anyMatch(u -> Objects.equals(u.getCpr(), azureUser.employeeId));
				}

				if (!exists) {
					disableUser(azureUser.id, azureUser.mailNickname);
				}
			}
		}
	}

	public boolean accountExists(String username) throws Exception {
		String upn = username + "@" + config.getSyncSettings().getDomain();
		UserCollectionPage userCollection = appClient.users()
				.buildRequest()
				.filter("userPrincipalName eq '" + upn + "'")
				.get();

		List<User> result = userCollection.getCurrentPage();
		if (result.size() > 1) {
			throw new Exception("More than one user with upn " + upn + " returned from Graph Api");
		}
		else {
			return result.size() == 1;
		}
	}

	public User createAccount(String username, DBUser user) throws Exception {
		if (config.getAzureAd().isUserDryRun()) {
			log.info("UserDryRun: would have created new Azure user with username " + username);
			return null;
		}

		User newUser = new User();
		newUser.accountEnabled = true;
		newUser.givenName = user.getFirstName();
		newUser.surname = user.getFamilyName();
		newUser.displayName = user.getFirstName() + " " + user.getFamilyName();
		newUser.mailNickname = username;
		newUser.userPrincipalName = username + "@" + config.getSyncSettings().getDomain();
		newUser.mail = username + "@" + config.getSyncSettings().getDomain();

		// TODO should maybe be changed to be configurable fields later. The fields needs to be possible to $filter (https://learn.microsoft.com/en-us/graph/aad-advanced-queries?tabs=http#user-properties)
		if (!config.getSyncSettings().isUseUsernameAsKey()) {
			newUser.employeeId = user.getCpr();
		}

		switch (config.getSyncSettings().getUniIdField()) {
			case DEPARTMENT -> newUser.department = user.getUniId();
			case EMPLOYEE_ID -> {
				if (!config.getSyncSettings().isUseUsernameAsKey()) {
					throw new Exception("Something is wrong with the configuration. Both using employeeId field for cpr and uniId");
				}
				newUser.employeeId = user.getUniId();
			}
		}

		newUser.companyName = "OS2skoledata";

		PasswordProfile passwordProfile = new PasswordProfile();
		passwordProfile.forceChangePasswordNextSignIn = true;
		passwordProfile.password = UUID.randomUUID().toString();
		newUser.passwordProfile = passwordProfile;

		User createdUser;
		try {
			createdUser = createUser(newUser);
		}
		catch (Exception e) {
			throw new Exception("Failed to create user with LocalPersonId " + user.getLocalPersonId(), e);
		}

		user.setAzureId(createdUser.id);

		return createdUser;
	}

	public void updateAccount(DBUser user, User match) throws Exception {
		User userToUpdate = new User();
		boolean changes = false;

		// TODO måske flere felter
		if (match.givenName == null || !match.givenName.equals(user.getFirstName())) {
			userToUpdate.givenName = user.getFirstName();
			log.info("Will update givenName on user with username " + user.getUsername() + " to " + user.getFirstName());
			changes = true;
		}
		if (match.surname == null || !match.surname.equals(user.getFamilyName())) {
			userToUpdate.surname = user.getFamilyName();
			log.info("Will update surname on user with username " + user.getUsername() + " to " + user.getFamilyName());
			changes = true;
		}
		if (match.displayName == null || !match.displayName.equals(user.getFirstName() + " " + user.getFamilyName())) {
			userToUpdate.displayName = user.getFirstName() + " " + user.getFamilyName();
			log.info("Will update displayName on user with username " + user.getUsername() + " to " + user.getFirstName() + " " + user.getFamilyName());
			changes = true;
		}
		log.info("match.accountEnabled current value: " + match.accountEnabled);
		if (match.accountEnabled == null || match.accountEnabled.equals(false)) {
			userToUpdate.accountEnabled = true;
			log.info("Will enable user with username " + user.getUsername());
			changes = true;
		}
		log.info("match.companyName current value: " + match.companyName);
		if (match.companyName == null || !match.companyName.equals("OS2skoledata")) {
			userToUpdate.companyName = "OS2skoledata";
			log.info("Will update companyName on user with username " + user.getUsername() + " to OS2skoledata");
			changes = true;
		}
		if (match.userPrincipalName == null || !match.userPrincipalName.equals(user.getUsername() + "@" + config.getSyncSettings().getDomain())) {
			userToUpdate.userPrincipalName = user.getUsername() + "@" + config.getSyncSettings().getDomain();
			log.info("Will update userPrincipalName on user with username " + user.getUsername() + " to " + user.getUsername() + "@" + config.getSyncSettings().getDomain());
			changes = true;
		}
		if (match.mail == null || !match.mail.equals(user.getUsername() + "@" + config.getSyncSettings().getDomain())) {
			userToUpdate.userPrincipalName = user.getUsername() + "@" + config.getSyncSettings().getDomain();
			log.info("Will update mail on user with username " + user.getUsername() + " to " + user.getUsername() + "@" + config.getSyncSettings().getDomain());
			changes = true;
		}

		// set UNI-ID
		switch (config.getSyncSettings().getUniIdField()) {
			case DEPARTMENT -> {
				if (match.department == null || !match.department.equals(user.getUniId())) {
					userToUpdate.department = user.getUniId();
					log.info("Will update department (UniId Field) on user with username " + user.getUsername() + " to " + user.getUniId());
					changes = true;
				}
			}
			case EMPLOYEE_ID -> {
				if (!config.getSyncSettings().isUseUsernameAsKey()) {
					throw new Exception("Something is wrong with the configuration. Both using employeeId field for cpr and uniId");
				}
				log.info("match.employeeId current value: " + match.employeeId);
				if (match.employeeId == null || !match.employeeId.equals(user.getUniId())) {
					userToUpdate.employeeId = user.getUniId();
					log.info("Will update employeeId (UniId Field) on user with username " + user.getUsername() + " to " + user.getUniId());
					changes = true;
				}
			}
		}

		if (changes) {
			try {
				updateUser(userToUpdate, match.id);

				if (!config.getAzureAd().isUserDryRun()) {
					log.info("Updated user with username " + user.getUsername());
				}
			}
			catch (Exception e) {
				throw new Exception("Failed to update user with LocalPersonId " + user.getLocalPersonId() + " and username " + user.getUsername(), e);
			}
		}
	}

	public void updateSecurityGroups(Institution institution, List<DBUser> users, List<DBGroup> classes, List<String> securityGroupIds, List<String> securityGroupIdsForRenamedGroups) throws Exception {
		log.info("Handling security groups for institution " + institution.getInstitutionName());
		Group institutionEmployeeGroup = handleGroupUpdate(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_AZURE_SECURITY_GROUP_ID, institution.getEmployeeAzureSecurityGroupId(), getInstitutionGroupName("EMPLOYEES", institution), null);
		Group institutionStudentGroup = handleGroupUpdate(institution, null, SetFieldType.INSTITUTION_STUDENT_AZURE_SECURITY_GROUP_ID, institution.getStudentAzureSecurityGroupId(), getInstitutionGroupName("STUDENTS", institution), null);
		Group institutionAllGroup = handleGroupUpdate(institution, null, SetFieldType.INSTITUTION_ALL_AZURE_SECURITY_GROUP_ID, institution.getAllAzureSecurityGroupId(), getInstitutionGroupName("ALL", institution), null);

		// handle group security group
		classes.sort((o1, o2) -> convertToInt(o2.getGroupLevel()) - convertToInt(o1.getGroupLevel()));
		for (DBGroup currentClass : classes) {
			List<DBUser> usersInClass = users.stream().filter(u -> u.getGroupIds().contains(String.valueOf(currentClass.getDatabaseId())) || (u.getStudentMainGroups() != null && u.getStudentMainGroups().contains(String.valueOf(currentClass.getDatabaseId())))).toList();
			Group classSecurityGroup = handleGroupUpdate(null, currentClass, SetFieldType.GROUP_AZURE_SECURITY_GROUP_ID, currentClass.getAzureSecurityGroupId(), getClassSecurityGroupName(currentClass, institution), securityGroupIdsForRenamedGroups);
			if (classSecurityGroup != null) {
				handleGroupMembers(classSecurityGroup, usersInClass, null);
				securityGroupIds.add(classSecurityGroup.id);
			}
		}

		// all users in institution security group
		if (institutionAllGroup != null) {
			handleGroupMembers(institutionAllGroup, users, null);
			securityGroupIds.add(institutionAllGroup.id);
		}

		// all students in institution security group
		if (institutionStudentGroup != null) {
			List<DBUser> usersInStudent = users.stream().filter(u -> u.getRole().equals(Role.STUDENT)).toList();
			handleGroupMembers(institutionStudentGroup, usersInStudent, null);
			securityGroupIds.add(institutionStudentGroup.id);
		}

		// all employees in institution security group
		if (institutionEmployeeGroup != null) {
			List<DBUser> usersInEmployee = users.stream().filter(u -> u.getRole().equals(Role.EMPLOYEE) || u.getRole().equals(Role.EXTERNAL)).toList();
			handleGroupMembers(institutionEmployeeGroup, usersInEmployee, null);
			securityGroupIds.add(institutionEmployeeGroup.id);
		}

		log.info("Finished handling security groups for institution " + institution.getInstitutionName());
	}

	public void updateTeams(Institution institution, List<DBUser> users, List<DBGroup> classes, List<String> teamIds, List<String> teamIdsForRenamedTeams, List<DBUser> allDBUsers) throws Exception {
		if (config.getSyncSettings().getAzureTeamsSettings().isHandleTeams()) {
			log.info("Handling teams for institution " + institution.getInstitutionName());

			// handle group security group
			classes.sort((o1, o2) -> convertToInt(o2.getGroupLevel()) - convertToInt(o1.getGroupLevel()));
			for (DBGroup currentClass : classes) {
				List<DBUser> members = users.stream().filter(u -> u.getAzureId() != null && u.getUsername() != null && (u.getStudentMainGroups() != null && u.getStudentMainGroups().contains(String.valueOf(currentClass.getDatabaseId())) && u.getGlobalRole().equals(Role.STUDENT))).toList();
				List<DBUser> owners = users.stream().filter(u -> u.getAzureId() != null && u.getUsername() != null && u.getGroupIds().contains(String.valueOf(currentClass.getDatabaseId())) && (u.getGlobalRole().equals(Role.EMPLOYEE) || u.getGlobalRole().equals(Role.EXTERNAL))).collect(Collectors.toList());
				Team classTeam = handleTeamUpdate(null, currentClass, SetFieldType.GROUP_AZURE_TEAM_ID, currentClass.getAzureTeamId(), getClassTeamName(currentClass, institution), getClassTeamMail(currentClass, institution), teamIdsForRenamedTeams, members, owners, config.getSyncSettings().getAzureTeamsSettings().getClassTeamTemplate(), true);
				if (classTeam != null) {
					teamIds.add(classTeam.id);
				} else if (currentClass.getAzureTeamId() != null) {
					teamIds.add(currentClass.getAzureTeamId());
				}
			}

			// all employees in institution security group
			List<DBUser> members = users.stream().filter(u -> u.getAzureId() != null && u.getUsername() != null && (u.getRole().equals(Role.EMPLOYEE) || u.getRole().equals(Role.EXTERNAL)) && !u.getUsername().equalsIgnoreCase(institution.getTeamAdminUsername())).toList();
			List<DBUser> owners = allDBUsers.stream().filter(u -> u.getUsername() != null && u.getUsername().equalsIgnoreCase(institution.getTeamAdminUsername())).collect(Collectors.toList());
			Team institutionEmployeeTeam = handleTeamUpdate(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_AZURE_TEAM_ID, institution.getEmployeeAzureTeamId(), getInstitutionTeamName("EMPLOYEES", institution), getInstitutionTeamMail("EMPLOYEES", institution), null, members, owners, config.getSyncSettings().getAzureTeamsSettings().getEmployeeTeamTemplate(), false);
			if (institutionEmployeeTeam != null) {
				teamIds.add(institutionEmployeeTeam.id);
			} else if (institution.getEmployeeAzureTeamId() != null) {
				teamIds.add(institution.getEmployeeAzureTeamId());
			}

			log.info("Finished handling teams for institution " + institution.getInstitutionName());
		}
	}

	public void updateGlobalSecurityGroups(List<DBUser> allUsers, List<String> securityGroupIds, List<String> lockedUsernames) throws Exception {
		log.info("Handling global security groups");
		Group globalEmployeeSecurityGroup = updateGlobalSecurityGroup(config.getSyncSettings().getNameStandards().getGlobalEmployeeSecurityGroupName());
		Group globalStudentSecurityGroup = updateGlobalSecurityGroup(config.getSyncSettings().getNameStandards().getGlobalStudentSecurityGroupName());

		// employee
		if (globalEmployeeSecurityGroup != null) {
			List<DBUser> usersInEmployee = allUsers.stream().filter(u -> u.getRole().equals(Role.EMPLOYEE) || u.getRole().equals(Role.EXTERNAL)).toList();
			handleGroupMembers(globalEmployeeSecurityGroup, usersInEmployee, lockedUsernames);
			securityGroupIds.add(globalEmployeeSecurityGroup.id);
		}

		// student
		if (globalStudentSecurityGroup != null) {
			List<DBUser> usersInStudent = allUsers.stream().filter(u -> u.getRole().equals(Role.STUDENT)).toList();
			handleGroupMembers(globalStudentSecurityGroup, usersInStudent, lockedUsernames);
			securityGroupIds.add(globalStudentSecurityGroup.id);
		}
		
		log.info("Finished handling global security groups");
	}

	private Group updateGlobalSecurityGroup(String name) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return null;
		}

		List<Group> groups = getSecurityGroups();
		Group match = groups.stream().filter(g -> g.displayName.equals(name)).findAny().orElse(null);

		if (match == null) {
			try {
				match = handleCreateGroup(name);
			} catch (Exception e) {
				throw new Exception("Failed to create global security group with name " + name, e);
			}
		}
		else {

			// we can't really update anything
		}

		return match;
	}

	private void handleGroupMembers(Group group, List<DBUser> usersInClass, List<String> lockedUsernames) {
		log.info("Handling members for group " + group.displayName);
		List<User> members = getUserMembersOfGroup(group.id);
		List<String> azureIds = members.stream().filter(m -> m.id != null).map(m -> m.id).collect(Collectors.toList());
		List<String> usernames = usersInClass.stream().map(DBUser::getUsername).toList();

		// delete permissions
		handleDeleteMemberships(group, lockedUsernames, members, usernames, "member");

		// create permissions
		Set<DBUser> toAssign = usersInClass.stream().filter(u -> u.getAzureId() != null).collect(Collectors.toSet());
		handleCreateMemberships(group, toAssign, azureIds, "member");
	}

	private void handleTeamGroupMembers(Group group, List<DBUser> members, List<DBUser> owners) {
		log.info("Handling members for team group with name" + group.displayName);
		List<User> membersFromAzure = getUserMembersOfGroup(group.id);
		List<User> ownersFromAzure = getUserOwnersOfGroup(group.id);
		List<String> memberAzureIds = membersFromAzure.stream().filter(m -> m.id != null).map(m -> m.id).collect(Collectors.toList());
		List<String> ownerAzureIds = ownersFromAzure.stream().filter(m -> m.id != null).map(m -> m.id).collect(Collectors.toList());
		List<String> memberUsernames = members.stream().map(DBUser::getUsername).toList();
		List<String> ownerUsernames = owners.stream().map(DBUser::getUsername).toList();

		// delete member permissions
		handleDeleteMemberships(group, null, membersFromAzure, memberUsernames, "member");

		// delete owner permissions
		handleDeleteMemberships(group, null, ownersFromAzure, ownerUsernames, "member");

		// create member permissions
		Set<DBUser> toAssign = members.stream().filter(u -> u.getAzureId() != null).collect(Collectors.toSet());
		handleCreateMemberships(group, toAssign, memberAzureIds, "member");

		// create owner permissions
		Set<DBUser> toAssignOwner = owners.stream().filter(u -> u.getAzureId() != null).collect(Collectors.toSet());
		handleCreateMemberships(group, toAssignOwner, ownerAzureIds, "owner");
	}

	private void handleDeleteMemberships(Group group, List<String> lockedUsernames, List<User> membersFromAzure, List<String> usernames, String type) {
		for (User user : membersFromAzure) {
			if (user.userPrincipalName == null) {
				continue;
			}

			String username = user.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "");

			// if user is in locked institution, skip
			if (lockedUsernames != null && lockedUsernames.contains(username)) {
				continue;
			}

			if (!usernames.contains(username)) {
				if (config.getAzureAd().isUserDryRun()) {
					log.info("UserDryRun: would have removed " + type + " with UPN " + user.userPrincipalName + " from group " + group.displayName);
				} else {
					if (type.equals("owner")) {
						removeOwnerFromGroup(group.id, user.id);
					}
					else if (type.equals("member")) {
						removeMemberFromGroup(group.id, user.id);
					}

					log.info("Removed " + type + " with UPN " + user.userPrincipalName + " from group " + group.displayName);
				}
			}
		}
	}

	private void handleCreateMemberships(Group group, Set<DBUser> toAssign, List<String> azureIds, String type) {
		for (DBUser user : toAssign) {
			if (!azureIds.contains(user.getAzureId())) {
				if (config.getAzureAd().isUserDryRun()) {
					log.info("UserDryRun: would have added " + type + " with username " + user.getUsername() + " to group " + group.displayName);
				} else {
					if (type.equals("owner")) {
						addOwnerToGroup(group.id, user.getAzureId());
					}
					else if (type.equals("member")) {
						addMemberToGroup(group.id, user.getAzureId());
					}

					azureIds.add(user.getAzureId());
					log.info("Added " + type + " with username " + user.getUsername() + " to group " + group.displayName);
				}
			}
		}
	}

	private String getInstitutionGroupName(String type, Institution institution) throws Exception {
		String name = "";
		switch (type) {
			case "ALL":
				if (config.getSyncSettings().getNameStandards().getAllInInstitutionSecurityGroupNameStandard() != null) {
					name = config.getSyncSettings().getNameStandards().getAllInInstitutionSecurityGroupNameStandard()
							.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
							.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				}
				break;
			case "EMPLOYEES":
				if (config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionSecurityGroupNameStandard() != null) {
					name = config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionSecurityGroupNameStandard()
							.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
							.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				}
				break;
			case "STUDENTS":
				if (config.getSyncSettings().getNameStandards().getAllStudentsInInstitutionSecurityGroupNameStandard() != null) {
					name = config.getSyncSettings().getNameStandards().getAllStudentsInInstitutionSecurityGroupNameStandard()
							.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
							.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				}
				break;
			default:
				throw new Exception("Unknown institution security group name standard type: " + type);
		}

		name = escapeCharacters(name);

		if (name.length() > 256) {
			name = name.substring(0, 256);
		}

		return name;
	}

	private String getInstitutionTeamName(String type, Institution institution) throws Exception {
		String name = "";
		switch (type) {
			case "EMPLOYEES":
				if (config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionTeamNameStandard() != null) {
					name = config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionTeamNameStandard()
							.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
							.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				}
				break;
			default:
				throw new Exception("Unknown institution security group name standard type: " + type);
		}

		if (name.length() > 256) {
			name = name.substring(0, 256);
		}

		return name;
	}

	private String getInstitutionTeamMail(String type, Institution institution) throws Exception {
		String name = "";
		switch (type) {
			case "EMPLOYEES":
				if (config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionTeamMailStandard() != null) {
					name = config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionTeamMailStandard()
							.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
							.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				}
				break;
			default:
				throw new Exception("Unknown institution security group name standard type: " + type);
		}

		String mailNickname = name
				.replace("æ", "ae")
				.replace("ø", "oe")
				.replace("å", "aa");

		mailNickname = mailNickname.replaceAll("[^a-zA-Z\\d]*", "");

		if (mailNickname.length() > 64) {
			mailNickname = mailNickname.substring(0, 64);
		}

		return mailNickname;
	}

	private String getClassSecurityGroupName(DBGroup currentClass, Institution institution) {
		return getClassNameWithStandards(currentClass, institution, config.getSyncSettings().getNameStandards().getClassSecurityGroupNameStandard(), config.getSyncSettings().getNameStandards().getClassSecurityGroupNameStandardNoClassYear());
	}

	private String getClassTeamName(DBGroup currentClass, Institution institution) {
		return getClassNameWithStandards(currentClass, institution, config.getSyncSettings().getNameStandards().getClassTeamNameStandard(), config.getSyncSettings().getNameStandards().getClassTeamNameStandardNoClassYear());
	}

	private String getClassTeamMail(DBGroup currentClass, Institution institution) {
		String mailNickname = getClassNameWithStandards(currentClass, institution, config.getSyncSettings().getNameStandards().getClassTeamMailStandard(), config.getSyncSettings().getNameStandards().getClassTeamMailStandardNoClassYear());
		mailNickname = mailNickname
				.replace("æ", "ae")
				.replace("ø", "oe")
				.replace("å", "aa");

		mailNickname = mailNickname.replaceAll("[^a-zA-Z\\d]*", "");

		if (mailNickname.length() > 64) {
			mailNickname = mailNickname.substring(0, 64);
		}

		return mailNickname;
	}

	private String getClassNameWithStandards(DBGroup currentClass, Institution institution, String normalStandard, String noClassYearStandard) {
		String name = "";
		if (normalStandard != null) {
			if (currentClass.getStartYear() != 0 && currentClass.getLine() != null) {
				name = normalStandard
						.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
						.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber())
						.replace("{CLASS_NAME}", currentClass.getGroupName())
						.replace("{CLASS_ID}", currentClass.getGroupId())
						.replace("{CLASS_LEVEL}", currentClass.getGroupLevel())
						.replace("{CLASS_YEAR}", String.valueOf(currentClass.getStartYear()))
						.replace("{CLASS_LINE}", currentClass.getLine());
			} else {
				String nameStandard = null;
				if (noClassYearStandard == null || noClassYearStandard.isEmpty()) {
					nameStandard = normalStandard;
				} else {
					nameStandard = noClassYearStandard;
				}

				name = nameStandard
						.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
						.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber())
						.replace("{CLASS_NAME}", currentClass.getGroupName())
						.replace("{CLASS_ID}", currentClass.getGroupId())
						.replace("{CLASS_LEVEL}", currentClass.getGroupLevel());
			}
		}

		name = escapeCharacters(name);

		if (name.length() > 256) {
			name = name.substring(0, 256);
		}
		return name;
	}

	public Group handleGroupUpdate(Institution institution, DBGroup group, SetFieldType setFieldType, String groupId, String name, List<String> securityGroupIdsForRenamedGroups) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return null;
		}

		log.info("Checking if security group with name " + name + " and id " + groupId + " should be updated or created");
		Group match = null;
		boolean hasPrefix = false;
		if (groupId != null) {
			match = getGroup(groupId);
		}

		if (match == null) {
			if (securityGroupIdsForRenamedGroups != null) {
				name = "c_" + removePrefix(name, "c_");
				hasPrefix = true;
			}

			match = handleCreateGroup(name);

			if (institution != null) {
				os2skoledataService.setFields(institution.getDatabaseId(), EntityType.INSTITUTION, setFieldType, match.id);
			}
			else if (group != null) {
				os2skoledataService.setFields(group.getDatabaseId(), EntityType.GROUP, setFieldType, match.id);
			}

			if (setFieldType.equals(SetFieldType.INSTITUTION_STUDENT_AZURE_SECURITY_GROUP_ID)) {
				institution.setStudentAzureSecurityGroupId(match.id);
			}
			else if (setFieldType.equals(SetFieldType.INSTITUTION_ALL_AZURE_SECURITY_GROUP_ID)) {
				institution.setAllAzureSecurityGroupId(match.id);
			}
			else if (setFieldType.equals(SetFieldType.INSTITUTION_EMPLOYEE_AZURE_SECURITY_GROUP_ID)) {
				institution.setEmployeeAzureSecurityGroupId(match.id);
			}
			else if (setFieldType.equals(SetFieldType.GROUP_AZURE_SECURITY_GROUP_ID)) {
				group.setAzureSecurityGroupId(match.id);
			}

			log.info("Created security group with name " + name + " and id " + groupId);
		}
		else {
			if (!match.displayName.equals(name)) {
				if (securityGroupIdsForRenamedGroups != null) {
					name = "c_" + removePrefix(name, "c_");
					hasPrefix = true;
				}

				callUpdateGroup(name, match, null);
				log.info("Updated security group with name " + name + " and id " + groupId);
			}
		}

		if (hasPrefix)
		{
			securityGroupIdsForRenamedGroups.add(match.id);
		}

		return match;
	}

	public Team handleTeamUpdate(Institution institution, DBGroup group, SetFieldType setFieldType, String teamId, String name, String mailNickmame, List<String> idsForRenamedTeamsAndGroups, List<DBUser> members, List<DBUser> owners, String template, boolean isEducationClass) throws Exception {
		if (StringUtils.isEmpty(name)) {
			return null;
		}

		if (owners.isEmpty()) {
			log.warn("Won't create or update group and team with name " + name + " for instituion " + institution.getInstitutionName() + " - no owners found.");
			return null;
		}

		log.info("Checking if team and group for team with name " + name + " and id " + teamId + " should be updated or created");
		Team matchTeam = null;
		Group matchGroup = null;
		boolean hasPrefix = false;
		if (teamId != null) {
			matchGroup = getGroup(teamId);
			matchTeam = getTeam(teamId);
		}

		if (matchGroup == null || matchTeam == null) {

			// do not add prefix on create - if we do the sharepoint page will have c_ in its name forever - not good
			matchGroup = handleCreateTeamAndGroup(mailNickmame, name, template, owners);

			// the group and the team based on the group will have the same ids
			if (institution != null) {
				os2skoledataService.setFields(institution.getDatabaseId(), EntityType.INSTITUTION, setFieldType, matchGroup.id);
				if (setFieldType.equals(SetFieldType.INSTITUTION_EMPLOYEE_AZURE_TEAM_ID)) {
					institution.setEmployeeAzureTeamId(matchGroup.id);
				}
			}
			else if (group != null) {
				os2skoledataService.setFields(group.getDatabaseId(), EntityType.GROUP, setFieldType, matchGroup.id);
				if (setFieldType.equals(SetFieldType.GROUP_AZURE_TEAM_ID)) {
					group.setAzureTeamId(matchGroup.id);
				}
			}

			log.info("Created team and group with name " + name + " and id " + matchGroup.id);

		} else {
			if (Boolean.TRUE.equals(matchTeam.isArchived)) {
				reactivateTeam(matchTeam.id);
			}

			if (!matchTeam.displayName.equals(name)) {
				if (idsForRenamedTeamsAndGroups != null) {
					name = "c_" + removePrefix(name, "c_");
					hasPrefix = true;
				}

				callUpdateTeam(name, matchTeam);
				log.info("Updated team with name " + name + " and id " + teamId);
			}

			boolean changes = false;
			if (!matchGroup.displayName.equals(name)) {
				if (idsForRenamedTeamsAndGroups != null) {
					name = "c_" + removePrefix(name, "c_");
					hasPrefix = true;
				}
				changes = true;
			}

			if (!matchGroup.mailNickname.toLowerCase().equals(mailNickmame.toLowerCase())) {
				changes = true;
			}

			if (changes) {
				callUpdateGroup(name, matchGroup, mailNickmame);
				log.info("Updated team with name " + name + " and id " + teamId);
			}
		}

		handleTeamGroupMembers(matchGroup, members, owners);

		if (hasPrefix)
		{
			idsForRenamedTeamsAndGroups.add(matchTeam.id);
		}

		return matchTeam;
	}

	private Group handleCreateTeamAndGroup(String mailNickmame, String name, String template, List<DBUser> owners) {
		Team newTeam = new Team();
		newTeam.description = "ManagedByOS2skoledata";
		newTeam.displayName = name;

		DBUser owner = owners.get(0);
		JsonArray membersArray = new JsonArray();
		JsonObject memberObject = new JsonObject();
		memberObject.addProperty("@odata.type", "#microsoft.graph.aadUserConversationMember");
		JsonArray rolesArray = new JsonArray();
		rolesArray.add(new JsonPrimitive("owner"));
		memberObject.add("roles", rolesArray);
		memberObject.addProperty("user@odata.bind", String.format("https://graph.microsoft.com/v1.0/users('%s')", owner.getAzureId()));
		membersArray.add(memberObject);

		newTeam.additionalDataManager().put("template@odata.bind", new JsonPrimitive(String.format("https://graph.microsoft.com/v1.0/teamsTemplates('%s')", template)));
		newTeam.additionalDataManager().put("members", membersArray);

		// this will not return a full team but it returns some headers that can be used for finding the id
		Team team = createTeam(newTeam);
		JsonElement teamHeaderValue = team.additionalDataManager().get("graphResponseHeaders");
		JsonObject jsonObject = teamHeaderValue.getAsJsonObject();
		String contentLocation = jsonObject.getAsJsonArray("content-location").get(0).getAsString();
		String id = contentLocation.replace("/teams('", "").replace("')", "");

		team = getTeam(id);

		Group group = getGroup(team.id);

		Group updatedGroup = new Group();
		updatedGroup.mailNickname = mailNickmame;
		updatedGroup.displayName = name;

		group = updateGroup(updatedGroup, group.id);

		return group;
	}

	public void deleteNotNeededGroups(List<String> securityGroupIds, List<String> lockedGroupIds, List<String> securityGroupIdsForRenamedGroups) {
		List<Group> allGroupsManagedByOS2skoledata = getSecurityGroups().stream().filter(g -> g.description != null && g.description.contains("ManagedByOS2skoledata")).toList();
		for (Group group : allGroupsManagedByOS2skoledata) {

			// if group is in locked institution, skip
			if (lockedGroupIds.contains(group.id)) {
				continue;
			}

			if (!securityGroupIds.contains(group.id)) {
				deleteGroup(group.id, false, group.displayName);
			}

			// check if group has prefix and remove it
			if (securityGroupIdsForRenamedGroups.contains(group.id) && group.displayName != null && group.displayName.startsWith("c_")) {
				Group renamedGroup = new Group();
				renamedGroup.displayName = removePrefix(group.displayName, "c_");
				updateGroup(renamedGroup, group.id);
			}
		}
	}

	public void archiveAndDeleteNotNeededTeams(List<String> teamIds, List<String> lockedTeamIds, List<String> teamIdsForRenamedTeams) {
		if (config.getSyncSettings().getAzureTeamsSettings().isHandleTeams()) {
			// note thate teams and the group they are based on will have the same ids. Therefore we only need to fetch the teams
			List<Team> allTeamsManagedByOS2skoledata = getTeams().stream().filter(t -> t.description != null && t.description.contains("ManagedByOS2skoledata")).toList();
			DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate today = LocalDate.now();
			for (Team lightTeam : allTeamsManagedByOS2skoledata) {
				// we need to fetch the team because isArchived can not be returned in the bulk getTeams
				Team team = getTeam(lightTeam.id);

				if (Boolean.TRUE.equals(team.isArchived)) {
					try {
						String[] nameSplit = team.displayName.split("-");
						String dateString = nameSplit[1] + "-" + nameSplit[2] + "-" + nameSplit[3];
						LocalDate archivedDate = LocalDate.parse(dateString, dateformatter);
						LocalDate toDeleteDate = archivedDate.plusDays(config.getSyncSettings().getAzureTeamsSettings().getReadOnlyPeriod());
						if (toDeleteDate.isBefore(today)) {
							deleteGroup(team.id, true, team.displayName);
						}
					} catch (Exception ex) {
						log.warn("Found archived Team " + team.displayName + " with id " + team.id + " but could not check date. Will not be deleted by us.", ex);
					}
				} else {
					// if team is in locked institution or already archived, skip
					if (lockedTeamIds.contains(team.id) || Boolean.TRUE.equals(team.isArchived)) {
						continue;
					}

					if (!teamIds.contains(team.id)) {
						renameGroupReadOnly(team.displayName, team.id);
						renameTeamReadOnly(team.displayName, team.id);
						archiveTeam(team.id);
					}

					// check if team has prefix and remove it
					if (teamIdsForRenamedTeams.contains(team.id) && team.displayName != null && team.displayName.startsWith("c_")) {
						Group renamedGroup = new Group();
						renamedGroup.displayName = removePrefix(team.displayName, "c_");
						updateGroup(renamedGroup, team.id);

						Team renamedTeam = new Team();
						renamedTeam.displayName = removePrefix(team.displayName, "c_");
						updateTeam(renamedTeam, team.id);
					}
				}
			}
		}
	}

	public List<Team> getTeams() {
		TeamCollectionPage teamCollection = appClient.teams()
				.buildRequest()
				.get();

		List<Team> result = new ArrayList<>();
		while (teamCollection != null) {
			result.addAll(teamCollection.getCurrentPage());
			final TeamCollectionRequestBuilder nextPage = teamCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				teamCollection = nextPage.buildRequest().get();
			}
		}

		return result;
	}

	public List<Group> getGroups() {
		GroupCollectionPage groupCollectionPage = appClient.groups()
				.buildRequest()
				.get();

		List<Group> result = new ArrayList<>();
		while (groupCollectionPage != null) {
			result.addAll(groupCollectionPage.getCurrentPage());
			final GroupCollectionRequestBuilder nextPage = groupCollectionPage.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				groupCollectionPage = nextPage.buildRequest().get();
			}
		}

		return result;
	}

	private void renameTeamReadOnly(String orgName, String id) {
		Team team = new Team();
		team.displayName = "Arkiveret-" + LocalDate.now().toString() + "-" +  orgName;

		updateTeam(team, id);
	}

	private void renameGroupReadOnly(String orgName, String id) {
		Group group = new Group();
		group.displayName = "Arkiveret-" + LocalDate.now().toString() + "-" +  orgName;

		updateGroup(group, id);
	}

	private Team createTeam(Team team) {
		return appClient.teams().buildRequest().post(team);
	}

	public Team updateTeam(Team team, String id) {
		return appClient.teams(id)
				.buildRequest()
				.patch(team);
	}

	public void archiveTeam(String teamId) {
		appClient.teams(teamId)
				.archive(new TeamArchiveParameterSet())
				.buildRequest()
				.post();
	}

	public void reactivateTeam(String teamId) {
		appClient.teams(teamId)
				.unarchive()
				.buildRequest()
				.post();

		log.info("Reactivated team with id " + teamId);
	}

	public Team getTeam(String id) {
		try {
			return appClient.teams(id).buildRequest().get();
		} catch (Exception ex) {
			// throws exception if not found
			return null;
		}
	}

	public List<ConversationMember> getMembersOfTeamAsConversationMembers(String id) {
		ConversationMemberCollectionPage conversationMemberCollection = appClient.teams(id).members().buildRequest().get();

		// returns 100 members pr page
		List<ConversationMember> result = new ArrayList<>();
		while (conversationMemberCollection != null) {
			result.addAll(conversationMemberCollection.getCurrentPage());
			final ConversationMemberCollectionRequestBuilder nextPage = conversationMemberCollection.getNextPage();
			if (nextPage == null) {
				break;
			}
			else {
				conversationMemberCollection = nextPage.buildRequest().get();
			}
		}

		log.info("Fetched " + result.size() + " members from team with id " + id);
		return result;
	}

	public List<AadUserConversationMember> getAllTeamMembers(String teamId) {
		List<AadUserConversationMember> allMembers = new ArrayList<>();

		ConversationMemberCollectionPage currentPage = appClient.teams(teamId)
				.members()
				.buildRequest()
				.get();

		while (currentPage != null) {
			for (ConversationMember member : currentPage.getCurrentPage()) {
				if (member instanceof AadUserConversationMember) {
					AadUserConversationMember aadMember = (AadUserConversationMember) member;
					allMembers.add(aadMember);
				}
			}

			ConversationMemberCollectionRequestBuilder nextPage = currentPage.getNextPage();
			currentPage = (nextPage != null) ? nextPage.buildRequest().get() : null;
		}

		log.info("Fetched " + allMembers.size() + " AadUserConversationMembers from team with id " + teamId);
		return allMembers;
	}

	public void addMemberToTeam(String teamId, String userId, String role) {
		AadUserConversationMember member = new AadUserConversationMember();
		member.userId = userId;
		member.roles = Arrays.asList(role);

		appClient.teams(teamId)
				.members()
				.buildRequest()
				.post(member);
	}

	private void updateTeamMember(Team team, AadUserConversationMember member, String role) {
		member.roles.clear();
		member.roles.add(role);
		appClient.teams(team.id)
				.members(member.id)
				.buildRequest()
				.patch(member);
	}

	public void removeMemberFromTeam(String teamId, String membershipIdToRemove) {
		appClient.teams(teamId)
				.members(membershipIdToRemove)
				.buildRequest()
				.delete();
	}

	private String removePrefix(String name, String prefix) {
		if (name.startsWith(prefix))
		{
			return name.substring(prefix.length());
		}
		else
		{
			return name; // Return the original name if the prefix is not present
		}
	}

	private void callUpdateGroup(String name, Group match, String mail) throws Exception {
		Group groupToUpdate = new Group();
		groupToUpdate.displayName = name;

		if (mail == null) {
			String mailNickname = name
					.replace("æ", "ae")
					.replace("ø", "oe")
					.replace("å", "aa");

			mailNickname = mailNickname.replaceAll("[^a-zA-Z\\d]*", "");

			if (mailNickname.length() > 64) {
				mailNickname = mailNickname.substring(0, 64);
			}
			groupToUpdate.mailNickname = mailNickname;
		} else {
			groupToUpdate.mailNickname = mail;
		}

		try {
			updateGroup(groupToUpdate, match.id);
		}
		catch (Exception e) {
			throw new Exception("Failed to update group with azure id " + match.id, e);
		}
	}

	private void callUpdateTeam(String name, Team match) throws Exception {
		Team teamToUpdate = new Team();
		teamToUpdate.displayName = name;

		try {
			updateTeam(teamToUpdate, match.id);
		}
		catch (Exception e) {
			throw new Exception("Failed to update Team with azure id " + match.id, e);
		}
	}

	private Group handleCreateGroup(String name) throws Exception {
		String mailNickname = name
				.replace("æ", "ae")
				.replace("ø", "oe")
				.replace("å", "aa");

		mailNickname = mailNickname.replaceAll("[^a-zA-Z\\d]*", "");

		if (mailNickname.length() > 64) {
			mailNickname = mailNickname.substring(0, 64);
		}

		Group newGroup = new Group();
		newGroup.displayName = name;
		newGroup.groupTypes = new ArrayList<>();
		newGroup.mailEnabled = false;
		newGroup.securityEnabled = true;
		newGroup.description = "ManagedByOS2skoledata";

		// TODO this has to be unique. I don't think we are ensuring that
		newGroup.mailNickname = mailNickname;

		try {
			return createGroup(newGroup);
		}
		catch (Exception e) {
			throw new Exception("Failed to create securityGroup with name " + name, e);
		}
	}

	private Group handleCreateGroupForTeam(String name, String mailNickname) throws Exception {
		Group newGroup = new Group();
		newGroup.displayName = name;
		newGroup.groupTypes = new ArrayList<>();
		newGroup.groupTypes.add("Unified");
		newGroup.mailEnabled = true;
		newGroup.securityEnabled = false;
		newGroup.description = "ManagedByOS2skoledata";
		newGroup.mailNickname = mailNickname;
		newGroup.visibility = "HiddenMembership";

		JsonArray resourceBehaviorOptions = new JsonArray();
		resourceBehaviorOptions.add("HideGroupInOutlook");
		resourceBehaviorOptions.add("WelcomeEmailDisabled");
		resourceBehaviorOptions.add("ConnectorsDisabled");
		resourceBehaviorOptions.add("SubscribeNewGroupMembers");

		newGroup.additionalDataManager().put("resourceBehaviorOptions", resourceBehaviorOptions);

		JsonArray creationOptions = new JsonArray();
		creationOptions.add("Team");
		creationOptions.add("classAssignments");

		newGroup.additionalDataManager().put("creationOptions", creationOptions);

		try {
			return createGroup(newGroup);
		}
		catch (Exception e) {
			throw new Exception("Failed to create securityGroup with name " + name, e);
		}
	}

	private EducationClass handleCreateEducationClass(String name, String mailNickname, DBGroup group) throws Exception {
		EducationClass educationClass = new EducationClass();
		educationClass.displayName = name;
		educationClass.mailNickname = mailNickname;
		educationClass.description = "ManagedByOS2skoledata";

		try {
			return createEducationClass(educationClass);
		}
		catch (Exception e) {
			throw new Exception("Failed to create educationClass with name " + name, e);
		}
	}

	private Team activateTeam(String id) throws Exception {
		Team toActivate = new Team();
		toActivate.additionalDataManager().put("isMembershipLimitedToOwners", new JsonPrimitive("false"));

		try {
			return updateTeam(toActivate, id);
		}
		catch (Exception e) {
			throw new Exception("Failed to activate team with id " + id, e);
		}
	}

	// this is not needed in AAD, but we do it in AD, so to make sure groups has the same names do it here as well
	private String escapeCharacters(String name) {
		name = name.replace("+", "\\+");
		name = name.replace(",", "\\,");
		name = name.replace("\"", "\\\"");
		name = name.replace("<", "\\<");
		name = name.replace(">", "\\>");
		name = name.replace(";", "\\;");
		name = name.replace("#", "\\#");
		name = name.replace("&", " og ");
		name = name.replace("/", " ");
		name = name.replace(".", "");

		return name;
	}

	private static int convertToInt(String level) {
		try {
			return Integer.parseInt(level);
		} catch (NumberFormatException e) {
			return Integer.MIN_VALUE;
		}
	}
}
