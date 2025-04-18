package dk.digitalidentity.service;

import com.microsoft.graph.models.User;
import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
import dk.digitalidentity.service.model.DBGroup;
import dk.digitalidentity.service.model.Institution;
import dk.digitalidentity.service.model.DBUser;
import dk.digitalidentity.service.model.ModificationHistory;
import dk.digitalidentity.service.model.enums.Action;
import dk.digitalidentity.service.model.enums.EntityType;
import dk.digitalidentity.service.model.enums.Role;
import dk.digitalidentity.service.model.enums.SetFieldType;
import dk.digitalidentity.service.model.enums.StudentRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class OS2skoledataService {

	@Autowired
	private OS2skoledataAzureADConfiguration config;

	public List<Institution> getInstitutions() throws Exception {
		log.info("Fetching institutions");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/institutions";

		ResponseEntity<Institution[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, Institution[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch institutions. Will not update");
		}

		log.info("Finished fetching institutions");
		return Arrays.asList(response.getBody());
	}

	public List<DBGroup> getClassesForInstitution(Institution institution) throws Exception {
		log.info("Fetching classes for institutions " + institution.getInstitutionName());
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/groups?institutionNumber=" + institution.getInstitutionNumber();

		ResponseEntity<DBGroup[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, DBGroup[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch groups for institution " + institution.getInstitutionName() + ". Will not update");
		}

		log.info("Finished fetching classes for institutions " + institution.getInstitutionName());
		return Arrays.asList(response.getBody());
	}

	public List<DBUser> getUsersForInstitution(Institution institution) throws Exception {

		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/persons?institutionNumber=" + institution.getInstitutionNumber();

		ResponseEntity<DBUser[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, DBUser[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch institutions. Will not update");
		}

		log.info("Finished fetching users for institutions " + institution.getInstitutionName());
		return Arrays.asList(response.getBody());
	}

	record UsernameRequest(long personDatabaseId, String username) {}
	public void setUsernameOnUser(long databaseId, String username) throws Exception {
		if (config.getAzureAd().isUserDryRun()) {
			log.info("UserDryRun: Would set username " + username + " on user with database id " + databaseId + " in OS2skoledata.");
			return;
		}
		UsernameRequest usernameRequest = new UsernameRequest(databaseId, username);
		HttpEntity<UsernameRequest> request = new HttpEntity<>(usernameRequest, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/person/username";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set username on user in OS2skoledata. Message: " + response.getBody());
		}
	}

	public long getHead() throws Exception {
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/head";

		ResponseEntity<Long> response = new RestTemplate().exchange(query, HttpMethod.GET, request, Long.class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch head. Will not update");
		}

		return response.getBody();
	}

	record HeadRequest(long head) {}
	public void setHead(long head, String institutionNumber) throws Exception {
		if (config.getAzureAd().isUserDryRun()) {
			log.info("UserDryRun: Would set head " + head + " on institution " + institutionNumber);
			return;
		}
		HeadRequest headRequest = new HeadRequest(head);
		HttpEntity<HeadRequest> request = new HttpEntity<>(headRequest, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/head/" + institutionNumber;

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set head in OS2skoledata for institution: " + institutionNumber + ". Message: " + response.getBody());
		}
	}

	record SetFieldRequest(long id, EntityType entityType, SetFieldType fieldType, String value) {}
	public void setFields(long id, EntityType entityType, SetFieldType fieldType, String value) throws Exception {
		SetFieldRequest setFieldRequest = new SetFieldRequest(id, entityType, fieldType, value);
		HttpEntity<SetFieldRequest> request = new HttpEntity<>(setFieldRequest, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/setfield";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set field on in OS2skoledata. Message: " + response.getBody());
		}
	}

	public List<ModificationHistory> getChanges(String institutionNumber) throws Exception {
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/changes/" + institutionNumber;

		ResponseEntity<ModificationHistory[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, ModificationHistory[].class);
		if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
			return Arrays.asList(response.getBody());
		} else {
			if (response.getStatusCode().equals(HttpStatus.CONFLICT)) {
				throw new Exception("Do a full sync");
			} else {
				throw new Exception("Failed to fetch changes. Will not delta update");
			}
		}
	}

	record ChangeRequest(List<Long> ids){}
	public List<DBUser> getChangedUsers(List<Long> changedPersonIds) throws Exception {
		ChangeRequest error = new ChangeRequest(changedPersonIds);
		HttpEntity<ChangeRequest> request = new HttpEntity<>(error, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/changes/persons";

		ResponseEntity<DBUser[]> response = new RestTemplate().exchange(query, HttpMethod.POST, request, DBUser[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch changed users. Will not delta update");
		}
		return Arrays.asList(response.getBody());
	}

	public List<Institution> getChangedInstitutions(List<Long> changedInstitutionIds) throws Exception {
		ChangeRequest error = new ChangeRequest(changedInstitutionIds);
		HttpEntity<ChangeRequest> request = new HttpEntity<>(error, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/changes/persons";

		ResponseEntity<Institution[]> response = new RestTemplate().exchange(query, HttpMethod.POST, request, Institution[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch changed institutions. Will not delta update");
		}
		return Arrays.asList(response.getBody());
	}

	public List<DBGroup> getChangedGroups(List<Long> changedGroupIds) throws Exception {
		ChangeRequest error = new ChangeRequest(changedGroupIds);
		HttpEntity<ChangeRequest> request = new HttpEntity<>(error, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/changes/persons";

		ResponseEntity<DBGroup[]> response = new RestTemplate().exchange(query, HttpMethod.POST, request, DBGroup[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch changed groups. Will not delta update");
		}
		return Arrays.asList(response.getBody());
	}

	public List<String> getLockedUsernames() throws Exception {
		log.info("Fetching locked usernames");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/locked/usernames";

		ResponseEntity<String[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, String[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch locked usernames. Will not update");
		}

		log.info("Finished fetching locked usernames");
		return Arrays.asList(response.getBody());
	}

	public List<String> getAllUsernames() throws Exception {
		log.info("Fetching all usernames");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/usernames/all";

		ResponseEntity<String[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, String[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch all usernames. Will not update");
		}

		log.info("Finished fetching all usernames");
		return Arrays.asList(response.getBody());
	}

	public List<String> getLockedGroupIds() throws Exception {
		log.info("Fetching locked group ids");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/locked/groups/azure";

		ResponseEntity<String[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, String[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch locked group ids. Will not update");
		}

		log.info("Finished fetching locked group ids");
		return Arrays.asList(response.getBody());
	}

	public List<String> getLockedTeamIds() throws Exception {
		log.info("Fetching locked team ids");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/locked/teams/azure";

		ResponseEntity<String[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, String[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch locked team ids. Will not update");
		}

		log.info("Finished fetching locked team ids");
		return Arrays.asList(response.getBody());
	}

	record ActionRequest(String username, Action action, String integrationType) {}
	public void setActionOnUser(String username, Action action) throws Exception {
		if (config.getAzureAd().isUserDryRun()) {
			log.info("UserDryRun: Would set action " + action + " on user with " + username + " in OS2skoledata.");
			return;
		}
		ActionRequest actionRequest = new ActionRequest(username, action, "AZURE");
		HttpEntity<ActionRequest> request = new HttpEntity<>(actionRequest, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/person/action";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set action on user in OS2skoledata. Message: " + response.getBody());
		}
	}

	public List<String> getKeepAliveUsernames() throws Exception {
		log.info("Fetching keep alive usernames");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/keepalive";

		ResponseEntity<String[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, String[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch keep alive usernames. Will not update");
		}

		log.info("Finished fetching keep alive usernames");
		return Arrays.asList(response.getBody());
	}

	record SetGroupIdentifierRequest(long institutionId, String groupKey, String groupEmail) {}
	public void setGroupIdentifier(long institutionId, String groupKey, String value) throws Exception {
		SetGroupIdentifierRequest setGroupIdentifierRequest = new SetGroupIdentifierRequest(institutionId, groupKey, value);
		HttpEntity<SetGroupIdentifierRequest> request = new HttpEntity<>(setGroupIdentifierRequest, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/setgroupemail?integration=AZURE";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set identifier on group in OS2skoledata. Message: " + response.getBody());
		}
	}

	public String getSchemaId() throws Exception {
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/settings/azure/schemaId";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.GET, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to fetch schema id");
		}

		return response.getBody();
	}

	public void setSchemaId(String schemaId) throws Exception {
		HttpEntity<String> request = new HttpEntity<>(schemaId, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/settings/azure/schemaId";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set schemaID in OS2skoledata. Message: " + response.getBody());
		}
	}

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("apiKey", config.getOs2skoledata().getApiKey());
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		return headers;
	}
}
