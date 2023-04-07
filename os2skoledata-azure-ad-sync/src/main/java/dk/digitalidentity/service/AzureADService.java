package dk.digitalidentity.service;

import com.microsoft.graph.models.AssignedLicense;
import com.microsoft.graph.models.DirectoryObject;
import com.microsoft.graph.models.Group;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.UserAssignLicenseParameterSet;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.GroupCollectionPage;
import com.microsoft.graph.requests.GroupCollectionRequestBuilder;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserCollectionPage;

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

	public Map<String, List<String>> generateUsernameMap(List<String> allUsernames) throws Exception {
		Map<String, List<String>> map = new HashMap<>();
		for (String username : allUsernames) {
			String key = "";
			if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.AS_UNILOGIN)) {
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
			else if (config.getSyncSettings().getUsernameSettings().getUsernameStandard().equals(UsernameStandard.PREFIX_NAME_LAST)) {
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
				.select("id,givenName,surname,displayName,userPrincipalName,employeeId,companyName,accountEnabled")
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
				.select("id,givenName,surname,displayName,userPrincipalName,employeeId,companyName,accountEnabled")
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

	public User getUser(String id) {
		return appClient.users(id).buildRequest().get();
	}

	private User createUser(User user) {
		return appClient.users().buildRequest().post(user);
	}

	public void disableUser(String id, String identification) throws Exception {
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

	public Group getGroup(String id) {
		return appClient.groups(id).buildRequest().get();
	}

	public void updateGroup(Group group, String id) {
		appClient.groups(id).buildRequest().patch(group);
	}

	private void deleteGroup(String id) {
		appClient.groups(id).buildRequest().delete();
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

	public void addMemberToGroup(String groupId, String objectId) {
		DirectoryObject directoryObject = new DirectoryObject();
		directoryObject.id = objectId;

		appClient.groups(groupId).members().references()
				.buildRequest()
				.post(directoryObject);
	}

	public void removeMemberFromGroup(String groupId, String objectId) {
		appClient.groups(groupId).members(objectId).reference().buildRequest().delete();
	}

	public void disableInactiveUsers(List<DBUser> dbUsers, List<User> azureOS2skoledataUsers) throws Exception {
		for (User azureUser : azureOS2skoledataUsers) {
			if (azureUser.employeeId != null && azureUser.userPrincipalName != null) {
				String username = azureUser.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), "");
				boolean exists = dbUsers.stream().anyMatch(u -> u.getCpr().equals(azureUser.employeeId));
				if (!exists) {
					disableUser(azureUser.id, username);
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

		User newUser = new User();
		newUser.accountEnabled = true;
		newUser.givenName = user.getFirstName();
		newUser.surname = user.getFamilyName();
		newUser.displayName = user.getFirstName() + " " + user.getFamilyName();
		newUser.mailNickname = username;
		newUser.userPrincipalName = username + "@" + config.getSyncSettings().getDomain();
		newUser.mail = username + "@" + config.getSyncSettings().getDomain();

		// TODO should maybe be changed to be configurable fields later. The fields needs to be possible to $filter (https://learn.microsoft.com/en-us/graph/aad-advanced-queries?tabs=http#user-properties)
		newUser.employeeId = user.getCpr();
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
		if (!match.givenName.equals(user.getFirstName())) {
			userToUpdate.givenName = user.getFirstName();
			changes = true;
		}
		if (!match.surname.equals(user.getFamilyName())) {
			userToUpdate.surname = user.getFamilyName();
			changes = true;
		}
		if (match.accountEnabled.equals(false)) {
			userToUpdate.accountEnabled = true;
			changes = true;
		}
		if (match.companyName == null || !match.companyName.equals("OS2skoledata")) {
			userToUpdate.companyName = "OS2skoledata";
			changes = true;
		}

		if (changes) {
			try {
				updateUser(userToUpdate, match.id);
				log.info("Updated user with username " + user.getUsername());
			}
			catch (Exception e) {
				throw new Exception("Failed to update user with LocalPersonId " + user.getLocalPersonId() + " and username " + user.getUsername(), e);
			}
		}
	}

	public void updateSecurityGroups(Institution institution, List<DBUser> users, List<DBGroup> classes, List<String> securityGroupIds) throws Exception {
		log.info("Handling security groups for institution " + institution.getInstitutionName());
		Group institutionEmployeeGroup = handleGroupUpdate(institution, null, SetFieldType.INSTITUTION_EMPLOYEE_AZURE_SECURITY_GROUP_ID, institution.getEmployeeAzureSecurityGroupId(), getInstitutionGroupName("EMPLOYEES", institution));
		Group institutionStudentGroup = handleGroupUpdate(institution, null, SetFieldType.INSTITUTION_STUDENT_AZURE_SECURITY_GROUP_ID, institution.getStudentAzureSecurityGroupId(), getInstitutionGroupName("STUDENTS", institution));
		Group institutionAllGroup = handleGroupUpdate(institution, null, SetFieldType.INSTITUTION_ALL_AZURE_SECURITY_GROUP_ID, institution.getAllAzureSecurityGroupId(), getInstitutionGroupName("ALL", institution));

		// handle group security group
		for (DBGroup currentClass : classes) {
			List<DBUser> usersInClass = users.stream().filter(u -> u.getGroupIds().contains("" + currentClass.getDatabaseId())).toList();
			Group classSecurityGroup = handleGroupUpdate(null, currentClass, SetFieldType.GROUP_AZURE_SECURITY_GROUP_ID, currentClass.getAzureSecurityGroupId(), getClassSecurityGroupName(currentClass, institution));
			handleGroupMembers(classSecurityGroup, usersInClass);
			securityGroupIds.add(classSecurityGroup.id);
		}

		// all users in institution security group
		handleGroupMembers(institutionAllGroup, users);
		securityGroupIds.add(institutionAllGroup.id);

		// all students in institution security group
		List<DBUser> usersInStudent = users.stream().filter(u -> u.getRole().equals(Role.STUDENT)).toList();
		handleGroupMembers(institutionStudentGroup, usersInStudent);
		securityGroupIds.add(institutionStudentGroup.id);

		// all employees in institution security group
		List<DBUser> usersInEmployee = users.stream().filter(u -> u.getRole().equals(Role.EMPLOYEE) || u.getRole().equals(Role.EXTERNAL)).toList();
		handleGroupMembers(institutionEmployeeGroup, usersInEmployee);
		securityGroupIds.add(institutionEmployeeGroup.id);

		log.info("Finished handling security groups for institution " + institution.getInstitutionName());
	}

	public void updateGlobalSecurityGroups(List<DBUser> allUsers, List<String> securityGroupIds) throws Exception {
		log.info("Handling global security groups");
		Group globalEmployeeSecurityGroup = updateGlobalSecurityGroup(config.getSyncSettings().getNameStandards().getGlobalEmployeeSecurityGroupName());
		Group globalStudentSecurityGroup = updateGlobalSecurityGroup(config.getSyncSettings().getNameStandards().getGlobalStudentSecurityGroupName());

		// employee
		List<DBUser> usersInEmployee = allUsers.stream().filter(u -> u.getRole().equals(Role.EMPLOYEE) || u.getRole().equals(Role.EXTERNAL)).toList();
		handleGroupMembers(globalEmployeeSecurityGroup, usersInEmployee);
		securityGroupIds.add(globalEmployeeSecurityGroup.id);

		// student
		List<DBUser> usersInStudent = allUsers.stream().filter(u -> u.getRole().equals(Role.STUDENT)).toList();
		handleGroupMembers(globalStudentSecurityGroup, usersInStudent);
		securityGroupIds.add(globalEmployeeSecurityGroup.id);
		log.info("Finished handling global security groups");
	}

	private Group updateGlobalSecurityGroup(String name) throws Exception {
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

	private void handleGroupMembers(Group group, List<DBUser> usersInClass) {
		log.info("Handling members for group " + group.displayName);
		List<User> members = getUserMembersOfGroup(group.id);
		List<String> azureIds = members.stream().filter(m -> m.id != null).map(m -> m.id).toList();
		List<String> usernames = usersInClass.stream().map(DBUser::getUsername).toList();

		// delete permissions
		for (User user : members) {
			if (!usernames.contains(user.userPrincipalName.replace("@" + config.getSyncSettings().getDomain(), ""))) {
				removeMemberFromGroup(group.id, user.id);
				log.info("Removed member with UPN " + user.userPrincipalName + " from group " + group.displayName);
			}
		}

		// create permissions
		Set<String> toAssign = usersInClass.stream().filter(u -> u.getAzureId() != null && !azureIds.contains(u.getAzureId())).map(DBUser::getAzureId).collect(Collectors.toSet());
		for (String id : toAssign) {
			addMemberToGroup(group.id, id);
			log.info("Added member with Azure Id " + id + " to group " + group.displayName);
		}
	}

	private String getInstitutionGroupName(String type, Institution institution) throws Exception {
		String name = "";
		switch (type) {
			case "ALL":
				name = config.getSyncSettings().getNameStandards().getAllInInstitutionSecurityGroupNameStandard()
						.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
						.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				break;
			case "EMPLOYEES":
				name = config.getSyncSettings().getNameStandards().getAllEmployeesInInstitutionSecurityGroupNameStandard()
						.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
						.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				break;
			case "STUDENTS":
				name = config.getSyncSettings().getNameStandards().getAllStudentsInInstitutionSecurityGroupNameStandard()
						.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
						.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber());
				break;
			default:
				throw new Exception("Unknown institution security group name standard type: " + type);
		}

		if (name.length() > 256) {
			name = name.substring(0, 256);
		}

		return name;
	}

	private String getClassSecurityGroupName(DBGroup currentClass, Institution institution) {
		String name = config.getSyncSettings().getNameStandards().getClassSecurityGroupNameStandard()
				.replace("{INSTITUTION_NAME}", institution.getInstitutionName())
				.replace("{INSTITUTION_NUMBER}", institution.getInstitutionNumber())
				.replace("{CLASS_NAME}", currentClass.getGroupName())
				.replace("{CLASS_ID}", currentClass.getGroupId())
				.replace("{CLASS_LEVEL}", currentClass.getGroupLevel());

		if (name.length() > 256) {
			name = name.substring(0, 256);
		}

		return name;
	}

	public Group handleGroupUpdate(Institution institution, DBGroup group, SetFieldType setFieldType, String groupId, String name) throws Exception {
		log.info("Checking if security group with name " + name + " and id " + groupId + " should be updated or created");
		Group match = null;
		if (groupId != null) {
			match = getGroup(groupId);
		}

		if (match == null) {
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
				callUpdateGroup(name, match);
				log.info("Updated security group with name " + name + " and id " + groupId);
			}
		}

		return match;
	}


	public void deleteNotNeededGroups(List<String> securityGroupIds) {
		List<Group> allGroupsManagedByOS2skoledata = getSecurityGroups().stream().filter(g -> g.description != null && g.description.contains("ManagedByOS2skoledata")).toList();
		for (Group group : allGroupsManagedByOS2skoledata) {
			if (!securityGroupIds.contains(group.id)) {
				deleteGroup(group.id);
			}
		}
	}

	private void callUpdateGroup(String name, Group match) throws Exception {
		Group groupToUpdate = new Group();
		groupToUpdate.displayName = name;

		String mailNickname = name
				.replace("æ", "ae")
				.replace("ø", "oe")
				.replace("å", "aa");

		mailNickname = mailNickname.replaceAll("[^a-zA-Z\\d]*", "");

		if (mailNickname.length() > 64) {
			mailNickname = mailNickname.substring(0, 64);
		}
		groupToUpdate.mailNickname = mailNickname;

		try {
			updateGroup(groupToUpdate, match.id);
		}
		catch (Exception e) {
			throw new Exception("Failed to update securityGroup with azure id " + match.id, e);
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

		// TODO do we want some more useful data in the description?
		newGroup.description = name;

		try {
			return createGroup(newGroup);
		}
		catch (Exception e) {
			throw new Exception("Failed to create securityGroup with name " + name, e);
		}
	}
}
