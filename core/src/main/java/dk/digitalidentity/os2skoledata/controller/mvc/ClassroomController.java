package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.security.RequireClassroomAdmin;
import dk.digitalidentity.os2skoledata.service.ClassroomActionQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequireClassroomAdmin
@Controller
public class ClassroomController {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private ClassroomActionQueueService classroomActionQueueService;

	record ActionDTO(long id, LocalDateTime created, LocalDateTime performed, String action, String status, String username, String courseId, String errorMessage, String requester) {}
	@GetMapping("/ui/classrooms")
	public String list(Model model) {
		if (!configuration.getClassroomAdministration().isEnabled()) {
			return "redirect:/error";
		}

		List<ActionDTO> queue = classroomActionQueueService.getAll().stream().map(a -> new ActionDTO(
				a.getId(),
				a.getCreated(),
				a.getPerformed(),
				a.getAction().getMessage(),
				a.getStatus().getMessage(),
				a.getUsername(),
				a.getCourseId(),
				a.getErrorMessage(),
				a.getRequestedByUsername()
				)).collect(Collectors.toList());
		model.addAttribute("queue", queue);

		return "classrooms/queue";
	}

}
