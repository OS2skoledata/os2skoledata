package dk.digitalidentity.os2skoledata.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import dk.digitalidentity.os2skoledata.security.SecurityUtil;

@Controller
public class DefaultController {

	@GetMapping("/")
	public String index() {
		if (SecurityUtil.isUserLoggedIn()) {
			return "redirect:/ui/showQueue";
		}
		
		return "index";
	}
}
