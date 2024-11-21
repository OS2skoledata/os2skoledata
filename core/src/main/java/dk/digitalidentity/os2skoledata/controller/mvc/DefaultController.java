package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.config.Constants;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class DefaultController implements ErrorController {
	private ErrorAttributes errorAttributes = new DefaultErrorAttributes();

	@GetMapping("/")
	public String index() {
		if (SecurityUtil.isAuthenticated()) {
			if (SecurityUtil.hasRole(Constants.PARENT.toString())) {
				return "redirect:/ui/students/parents/changepassword";
			}

			return "redirect:/ui/showQueue";
		}

		return "index";
	}
	
	@RequestMapping(value = "/error", produces = "text/html")
	public String errorPage(Model model, HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(new ServletWebRequest(request));

		model.addAllAttributes(body);

		Object status = body.get("status");
		if (status != null && status instanceof Integer && (Integer) status == 403) {
			model.addAttribute("message", "Du har desværre ikke adgang til denne side. Det skyldes højst sandsynligt at du ikke har de rigtige rettigheder.");
		}
		
		Object authException = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

		// handle the forward case
		if (authException == null && request.getSession() != null) {
			authException = request.getSession().getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}

		if (authException != null && authException instanceof Throwable) {
			StringBuilder builder = new StringBuilder();
			body.put("exception", builder.toString());

			if (authException instanceof UsernameNotFoundException) {
				String message = ((UsernameNotFoundException) authException).getMessage();
				model.addAttribute("message", message);
			}
		}

		return "error";
	}

	@RequestMapping(value = "/error", produces = "application/json")
	public ResponseEntity<Map<String, Object>> errorJSON(HttpServletRequest request) {
		Map<String, Object> body = getErrorAttributes(new ServletWebRequest(request));

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		try {
			status = HttpStatus.valueOf((int) body.get("status"));
		}
		catch (Exception ex) {
			;
		}

		return new ResponseEntity<>(body, status);
	}

	private Map<String, Object> getErrorAttributes(WebRequest request) {
		return errorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());
	}
}
