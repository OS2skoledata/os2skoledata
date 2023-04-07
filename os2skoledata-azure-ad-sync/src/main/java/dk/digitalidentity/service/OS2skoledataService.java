package dk.digitalidentity.service;

import com.microsoft.graph.models.User;
import dk.digitalidentity.config.OS2skoledataAzureADConfiguration;
import dk.digitalidentity.service.model.DBGroup;
import dk.digitalidentity.service.model.Institution;
import dk.digitalidentity.service.model.DBUser;
import dk.digitalidentity.service.model.ModificationHistory;
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
		log.debug("Fetching institutions");
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/institutions";

		ResponseEntity<Institution[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, Institution[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch institutions. Will not update");
		}

		log.debug("Finished fetching institutions");
		return Arrays.asList(response.getBody());
	}

	public List<DBGroup> getClassesForInstitution(Institution institution) throws Exception {
		log.debug("Fetching classes for institutions " + institution.getInstitutionName());
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/groups?institutionNumber=" + institution.getInstitutionNumber();

		ResponseEntity<DBGroup[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, DBGroup[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch groups for institution " + institution.getInstitutionName() + ". Will not update");
		}

		log.debug("Finished fetching classes for institutions " + institution.getInstitutionName());
		return Arrays.asList(response.getBody());
	}

	public List<DBUser> getUsersForInstitution(Institution institution) throws Exception {

		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/persons?institutionNumber=" + institution.getInstitutionNumber();

		ResponseEntity<DBUser[]> response = new RestTemplate().exchange(query, HttpMethod.GET, request, DBUser[].class);
		if (!response.getStatusCode().equals(HttpStatus.OK) || response.getBody() == null) {
			throw new Exception("Failed to fetch institutions. Will not update");
		}

		log.debug("Finished fetching users for institutions " + institution.getInstitutionName());
		return Arrays.asList(response.getBody());
	}

	record UsernameRequest(String localPersonId, String username) {}
	public void setUsernameOnUser(String localPersonId, String username) throws Exception {
		UsernameRequest usernameRequest = new UsernameRequest(localPersonId, username);
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
	public void setHead(long head) throws Exception {
		HeadRequest headRequest = new HeadRequest(head);
		HttpEntity<HeadRequest> request = new HttpEntity<>(headRequest, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/head";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			throw new Exception("Failed to set head in OS2skoledata. Message: " + response.getBody());
		}
	}

	record ErrorRequest(String message) {}
	public void reportError(String message) {
		ErrorRequest error = new ErrorRequest(message);
		HttpEntity<ErrorRequest> request = new HttpEntity<>(error, getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/reporterror";

		ResponseEntity<String> response = new RestTemplate().exchange(query, HttpMethod.POST, request, String.class);
		if (!response.getStatusCode().equals(HttpStatus.OK)) {
			log.error("Failed to report error back to OS2skoledata. Message: " + response.getBody());
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

	public List<ModificationHistory> getChanges() throws Exception {
		HttpEntity<String> request = new HttpEntity<>(getHeaders());
		String query = config.getOs2skoledata().getBaseUrl() + "/api/changes";

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

	private HttpHeaders getHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("apiKey", config.getOs2skoledata().getApiKey());
		headers.add("Content-Type", "application/json");
		headers.add("Accept", "application/json");
		return headers;
	}
}
