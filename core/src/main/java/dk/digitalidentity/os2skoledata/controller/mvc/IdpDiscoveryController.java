package dk.digitalidentity.os2skoledata.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IdpDiscoveryController {

	@GetMapping("/login")
	public String index() {
		return "redirect:/discovery";
	}

	@GetMapping("/discovery")
	public String discovery(Model model) {
		return "index";
	}
}
