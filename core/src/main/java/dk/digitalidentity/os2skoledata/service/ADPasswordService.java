package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.framework.ad.service.ActiveDirectoryService;
import dk.digitalidentity.framework.ad.service.model.SetPasswordRequest;
import dk.digitalidentity.framework.ad.service.model.SetPasswordResponse;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.PasswordChangeQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ReplicationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ADPasswordService {

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private PasswordChangeQueueService passwordChangeQueueService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private ActiveDirectoryService activeDirectoryService;

	@Autowired
	private CprPasswordMappingService cprPasswordMappingService;

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
			List<DBInstitutionPerson> persons = institutionPersonService.findByUsernameAndDeletedFalse(change.getUsername());

			if (persons.size() == 0) {
				log.error("Person did not exist in database for username=" + change.getUsername());
				change.setMessage("Missing person in database");
				change.setStatus(ReplicationStatus.FINAL_ERROR);
				passwordChangeQueueService.save(change, false);
				continue;
			}

			DBInstitutionPerson firstPerson = persons.get(0);

			SetPasswordRequest setPasswordRequest = getSetPasswordRequest(change);
			if (setPasswordRequest == null) {
				change.setMessage("Failed to decrypt password");
				change.setStatus(ReplicationStatus.ERROR);
				passwordChangeQueueService.save(change, false);
				continue;
			}

			SetPasswordResponse setPasswordResponse = activeDirectoryService.setPassword(setPasswordRequest);
			SetPasswordResponse.PasswordStatus status = setPasswordResponse.getStatus();
			switch (status) {
				// inform user through UI (but also save result in queue for debugging purposes)
				case TECHNICAL_ERROR:
				case INSUFFICIENT_PERMISSION:
				case UNKNOWN_USER:
					// FINAL_ERROR prevent any retries on this
					change.setStatus(ReplicationStatus.FINAL_ERROR);
					change.setMessage(setPasswordResponse.getMessage());
					break;
				case OK:
					change.setStatus(ReplicationStatus.SYNCHRONIZED);
					change.setMessage(setPasswordResponse.getMessage());
					cprPasswordMappingService.setPassword(firstPerson.getPerson().getCivilRegistrationNumber(), change.getPassword());
					break;
				// delay replication in case of a timeout
				case TIMEOUT:
					change.setStatus(ReplicationStatus.ERROR);
					change.setMessage(setPasswordResponse.getMessage());
					if (change.getTts() != null && LocalDateTime.now().minusMinutes(10).isAfter(change.getTts())) {
						log.error("Replication failed, password change has not been replicated for more than 10 minutes (ID: " + change.getId() + ")");
						change.setStatus(ReplicationStatus.FINAL_ERROR);
					}
					else {
						log.debug("Password Replication failed, trying again in 1 minute (ID: " + change.getId() + ")");
					}
					break;
			}


			passwordChangeQueueService.save(change, ReplicationStatus.SYNCHRONIZED.equals(change.getStatus()));
		}
	}

	public SetPasswordRequest getSetPasswordRequest(PasswordChangeQueue change) {
		SetPasswordRequest setPasswordRequest = new SetPasswordRequest();
		setPasswordRequest.setUserId(change.getUsername());
		try {
			setPasswordRequest.setPassword(passwordChangeQueueService.decryptPassword(change.getPassword()));
		} catch (Exception e) {
			log.error("Failed to decrypt password", e);
			return null;
		}

		return setPasswordRequest;
	}

	public static boolean isCritical(SetPasswordResponse.PasswordStatus status) {
		switch (status) {
			case TECHNICAL_ERROR:
			case INSUFFICIENT_PERMISSION:
				return true;
			case OK:
			case TIMEOUT:
				return false;
			default:
				return true;
		}
	}
}
