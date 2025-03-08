package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.service.GhostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GhostController {

	@Autowired
	private GhostService ghostService;

	@GetMapping("/ui/keepalive")
	public String list(Model model) {
		model.addAttribute("ghosts", ghostService.getAllActive());

		return "ghosts/list";
	}
}
