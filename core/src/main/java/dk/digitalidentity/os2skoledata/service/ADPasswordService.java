package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.PasswordChangeQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ReplicationStatus;
import dk.digitalidentity.os2skoledata.service.model.ADPasswordRequest;
import dk.digitalidentity.os2skoledata.service.model.ADPasswordResponse;
import dk.digitalidentity.os2skoledata.service.model.ADPasswordResponse.ADPasswordStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ADPasswordService {

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private PasswordChangeQueueService passwordChangeQueueService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	public ADPasswordStatus attemptPasswordReplication(PasswordChangeQueue change, boolean fromUI) {
		try {
			ResponseEntity<ADPasswordResponse> response = restTemplate.exchange(getURL("api/setPassword") , HttpMethod.POST, getRequest(change), ADPasswordResponse.class);

			ADPasswordResponse result = response.getBody();
			if (result == null) {
				log.error("No result on response");
				return ADPasswordStatus.TECHNICAL_ERROR;
			}

			if (response.getStatusCodeValue() == 200 && ADPasswordStatus.OK.equals(result.getStatus())) {
				change.setStatus(ReplicationStatus.SYNCHRONIZED);
				change.setMessage(null);
			}
			else {
				// Setting status and message of change
				change.setStatus(ReplicationStatus.ERROR);
				String changeMessage = "Code: " + response.getStatusCode() + " Message: ";
				changeMessage += (result.getMessage() != null) ?  result.getMessage() : "NULL";
				change.setMessage(changeMessage);

				// Logging error/warn depending on how long it has gone unsynchronized
				if (change.getTts() != null && LocalDateTime.now().minusMinutes(10).isAfter(change.getTts())) {
					log.error("Replication failed, password change has not been replicated for more than 10 minutes (ID: " + change.getId() + ")");
					change.setStatus(ReplicationStatus.FINAL_ERROR);
				}
				else {
					// we won't try again in this case if called from UI
					if (fromUI && (ADPasswordStatus.FAILURE.equals(result.getStatus()) || ADPasswordStatus.TECHNICAL_ERROR.equals(result.getStatus()) || ADPasswordStatus.INSUFFICIENT_PERMISSION.equals(result.getStatus()))) {
						log.warn("Password Replication failed, won't try again. Failed for username: " + change.getUsername());
					} else {
						log.warn("Password Replication failed, trying again in 1 minute (ID: " + change.getId() + ")");
					}
				}
			}

			return result.getStatus();
		}
		catch (Exception ex) {
			change.setStatus(ReplicationStatus.ERROR);
			change.setMessage("Failed to connect to AD Password replication service: " + ex.getMessage());

			// tts null check to avoid issues with first attempt
			if (change.getTts() != null && LocalDateTime.now().minusMinutes(10).isAfter(change.getTts())) {
				log.error("Replication failed, password change has not been replicated for more than 10 minutes (ID: " + change.getId() + ")", ex);
				change.setStatus(ReplicationStatus.FINAL_ERROR);
			}
			else {
				// we won't try again in this case if called from UI
				if (fromUI) {
					log.warn("Password Replication failed, won't try again. Failed for username: " + change.getUsername());
				} else {
					log.warn("Password Replication failed, trying again in 1 minute (ID: " + change.getId() + ")");
				}

			}
		}

		return ADPasswordStatus.TECHNICAL_ERROR;
	}

	@Transactional(rollbackFor = Exception.class)
	public void syncQueueCleanupTask() {

		// delete successful replications and purposely not replicated after 7 days
		List<PasswordChangeQueue> synchronizedChanges = passwordChangeQueueService.getByStatus(ReplicationStatus.SYNCHRONIZED);
		synchronizedChanges.addAll(passwordChangeQueueService.getByStatus(ReplicationStatus.DO_NOT_REPLICATE));

		for (PasswordChangeQueue synchronizedChange : synchronizedChanges) {
			LocalDateTime maxRetention = LocalDateTime.now().minusDays(7);

			if (synchronizedChange.getTts().isBefore(maxRetention)) {
				passwordChangeQueueService.delete(synchronizedChange);
			}
		}

		// then delete failures after 21 days
		synchronizedChanges = passwordChangeQueueService.getByStatus(ReplicationStatus.FINAL_ERROR);

		for (PasswordChangeQueue synchronizedChange : synchronizedChanges) {
			LocalDateTime maxRetention = LocalDateTime.now().minusDays(21);

			if (synchronizedChange.getTts().isBefore(maxRetention)) {
				passwordChangeQueueService.delete(synchronizedChange);
			}
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void syncPasswordsToAD(List<PasswordChangeQueue> changes) {
		for (PasswordChangeQueue change : changes) {
			List<DBInstitutionPerson> persons = institutionPersonService.findByUsername(change.getUsername());

			if (persons.size() == 0) {
				log.error("Person did not exist in database for username=" + change.getUsername());
				change.setMessage("Missing person in database");
				change.setStatus(ReplicationStatus.FINAL_ERROR);
				passwordChangeQueueService.save(change, false);
				continue;
			}

			attemptPasswordReplication(change, false);

			passwordChangeQueueService.save(change, ReplicationStatus.SYNCHRONIZED.equals(change.getStatus()));
		}
	}

	private String getURL(String endpoint) {
		String url = configuration.getStudentAdministration().getWebSocketUrl();
		if (!url.endsWith("/")) {
			url += "/";
		}
		url += endpoint;

		return url;
	}

	private HttpEntity<ADPasswordRequest> getRequest(PasswordChangeQueue change) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {
		HttpHeaders headers = new HttpHeaders();
		headers.add("apiKey", configuration.getStudentAdministration().getWebSocketApiKey());

		ADPasswordRequest adPasswordRequest = new ADPasswordRequest(change, passwordChangeQueueService.decryptPassword(change.getPassword()), configuration.getStudentAdministration().getAdDomain());
		return new HttpEntity<>(adPasswordRequest, headers);
	}
}
