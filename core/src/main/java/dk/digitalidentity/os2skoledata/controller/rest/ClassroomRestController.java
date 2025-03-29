package dk.digitalidentity.os2skoledata.controller.rest;

import dk.digitalidentity.os2skoledata.dao.model.ClassroomActionQueue;
import dk.digitalidentity.os2skoledata.dao.model.enums.ClassroomAction;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.ClassroomActionQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClassroomRestController {

	@Autowired
	private ClassroomActionQueueService classroomActionQueueService;

	private record ClassroomActionDTO(ClassroomAction action, String course, String username) {}
	@ResponseBody
	@PostMapping("/rest/classrooms/action/save")
	public ResponseEntity<?> saveClassroomAction(@RequestBody ClassroomActionDTO dto) {
		if (dto.action == null || !StringUtils.hasLength(dto.course) || (dto.action.equals(ClassroomAction.TRANSFER) && !StringUtils.hasLength(dto.username))) {
			return new ResponseEntity<>("Alle felter skal være udfyldt", HttpStatus.BAD_REQUEST);
		}

		ClassroomActionQueue queue = new ClassroomActionQueue();
		queue.setRequestedByUsername(SecurityUtil.getUserId());
		queue.setAction(dto.action);
		queue.setCourseId(dto.course);

		if (dto.action.equals(ClassroomAction.TRANSFER)) {
			queue.setUsername(dto.username);
		}

		classroomActionQueueService.save(queue);

		return ResponseEntity.ok().build();
	}
}
