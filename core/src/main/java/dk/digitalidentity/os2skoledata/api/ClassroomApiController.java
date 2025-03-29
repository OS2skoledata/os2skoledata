package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.ClassroomActionQueue;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomAction;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomActionStatus;
import dk.digitalidentity.os2skoledata.service.ClassroomActionQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
public class ClassroomApiController {

	@Autowired
	private ClassroomActionQueueService classroomActionQueueService;

	record PendingChangesDTO(long id, String courseId, String username, ClassroomAction action) {}

	@GetMapping("/api/classrooms/pending")
	public ResponseEntity<?> getPendingChanges() {
		List<PendingChangesDTO> pendingChanges = classroomActionQueueService.getAllPending().stream()
				.map(a -> new PendingChangesDTO(
					a.getId(),
						a.getCourseId(),
						a.getUsername(),
						a.getAction()
				)).toList();
		return ResponseEntity.ok(pendingChanges);
	}

	record StatusDTO(long id, ClassroomActionStatus status, String errorMessage) {}
	@PostMapping("/api/classrooms/status")
	public ResponseEntity<?> setChangeStatus(@RequestBody StatusDTO dto) {
		ClassroomActionQueue change = classroomActionQueueService.getById(dto.id);
		if (change == null) {
			return new ResponseEntity<>("Failed to find change", HttpStatus.BAD_REQUEST);
		}

		if (!change.getStatus().equals(ClassroomActionStatus.WAITING)) {
			return new ResponseEntity<>("Tried to set status on not pending change", HttpStatus.BAD_REQUEST);
		}

		change.setStatus(dto.status);
		change.setPerformed(LocalDateTime.now());

		if (dto.status.equals(ClassroomActionStatus.FAILED)) {
			change.setErrorMessage(dto.errorMessage);
		}

		classroomActionQueueService.save(change);

		return new ResponseEntity<>(HttpStatus.OK);
	}
}
