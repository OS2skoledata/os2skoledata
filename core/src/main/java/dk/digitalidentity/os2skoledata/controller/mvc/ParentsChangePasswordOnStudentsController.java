package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.framework.ad.service.model.SetPasswordResponse;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.controller.mvc.dto.PasswordChangeForm;
import dk.digitalidentity.os2skoledata.controller.mvc.validator.PasswordChangeValidator;
import dk.digitalidentity.os2skoledata.dao.model.PasswordSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import dk.digitalidentity.os2skoledata.security.RequireParent;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.ADPasswordService;
import dk.digitalidentity.os2skoledata.service.AuditLogger;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.PasswordSettingService;
import dk.digitalidentity.os2skoledata.service.model.StudentPasswordChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@RequireParent
@Slf4j
@Controller
public class ParentsChangePasswordOnStudentsController {

	@Autowired
	private SecurityUtil securityUtil;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private PasswordSettingService passwordSettingService;

	@Autowired
	private PasswordChangeValidator passwordChangeFormValidator;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private AuditLogger auditLogger;

	@InitBinder("passwordForm")
	public void initClientBinder(WebDataBinder binder) {
		binder.setValidator(passwordChangeFormValidator);
	}

	@GetMapping("/ui/students/parents")
	public String index() {
		if (!configuration.getStudentAdministration().isEnabled() || !configuration.getStudentAdministration().isParentPasswordChangeEnabled()) {
			return "redirect:/error";
		}
		
		return "students/password-change-parent/welcome";
	}

	@GetMapping("/ui/students/parents/changepassword")
	public String studentList(Model model, RedirectAttributes redirectAttributes) {
		if (!configuration.getStudentAdministration().isEnabled() || !configuration.getStudentAdministration().isParentPasswordChangeEnabled()) {
			return "redirect:/error";
		}

		String cpr = securityUtil.getCpr();
		if (!StringUtils.hasLength(cpr)) {
			log.warn("Person ikke logget ind, session timeout?");
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Du er ikke logget ind");

			return "redirect:/ui/students/parents";
		}

		List<StudentPasswordChangeDTO> children = institutionPersonService.getChildrenPasswordAllowed(cpr);
		model.addAttribute("children", children);

		return "students/password-change-parent/list";
	}

	@GetMapping("/ui/students/parents/changepassword/{username}")
	public String changePassword(Model model, RedirectAttributes redirectAttributes, @PathVariable("username") String username) {
		if (!configuration.getStudentAdministration().isEnabled() || !configuration.getStudentAdministration().isParentPasswordChangeEnabled()) {
			return "redirect:/error";
		}

		String cpr = securityUtil.getCpr();
		if (!StringUtils.hasLength(cpr)) {
			log.warn("Person ikke logget ind, session timeout?");
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Du er ikke logget ind");

			return "redirect:/ui/students/parents";
		}

		List<StudentPasswordChangeDTO> children = institutionPersonService.getChildrenPasswordAllowed(cpr);
		StudentPasswordChangeDTO student = children.stream().filter(s -> s.getUsername().equals(username)).findAny().orElse(null);
		if (student == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Det er ikke tilladt at skifte kodeord på denne bruger");
			return "redirect:/ui/students/parents/changepassword";
		}

		PasswordSetting passwordSetting = passwordSettingService.getPasswordSettingsForUsername(username);
		if (passwordSetting == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Kodeordsreglerne tilknyttet denne bruger, kan ikke findes");
			return "redirect:/ui/students/parents/changepassword";
		}

		if (configuration.getStudentAdministration().isIndskolingSpecialEnabled() && Objects.equals(passwordSettingService.findGradeGroup(username), GradeGroup.YOUNGEST)) {
			model.addAttribute("passwordWords", passwordSettingService.getPasswordWords());
			model.addAttribute("passwordForm", new PasswordChangeForm(student));

			return "students/password-change-parent/change-password-youngStudent";
		} else {
			model.addAttribute("settings", passwordSetting);
			model.addAttribute("passwordForm", new PasswordChangeForm(student));

			return "students/password-change-parent/change-password";
		}
	}

	@PostMapping("/ui/students/parents/changepassword")
	public String changePassword(Model model, RedirectAttributes redirectAttributes, @Valid @ModelAttribute("passwordForm") PasswordChangeForm form, BindingResult bindingResult) {
		if (!configuration.getStudentAdministration().isEnabled() || !configuration.getStudentAdministration().isParentPasswordChangeEnabled()) {
			return "redirect:/error";
		}

		String cpr = securityUtil.getCpr();
		if (!StringUtils.hasLength(cpr)) {
			log.warn("Person ikke logget ind, session timeout?");
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Du er ikke logget ind");

			return "redirect:/ui/students/parents";
		}

		List<StudentPasswordChangeDTO> children = institutionPersonService.getChildrenPasswordAllowed(cpr);
		StudentPasswordChangeDTO student = children.stream().filter(s -> s.getUsername().equals(form.getUsername())).findAny().orElse(null);
		if (student == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Det er ikke tilladt at skifte kodeord på denne bruger");
			return "redirect:/ui/students/parents/changepassword";
		}

		// Check for password errors
		if (bindingResult.hasErrors()) {
			if (configuration.getStudentAdministration().isIndskolingSpecialEnabled() && Objects.equals(passwordSettingService.findGradeGroup(form.getUsername()), GradeGroup.YOUNGEST)) {
				model.addAttribute("passwordWords", passwordSettingService.getPasswordWords());
				model.addAttribute("passwordForm", new PasswordChangeForm(student));

				return "students/password-change-parent/change-password-youngStudent";
			} else {
				PasswordSetting passwordSetting = passwordSettingService.getPasswordSettingsForUsername(form.getUsername());
				if (passwordSetting == null) {
					redirectAttributes.addFlashAttribute("flashError", "Fejl! Kodeordsreglerne tilknyttet denne bruger, kan ikke findes");
					return "redirect:/ui/students/parents/changepassword";
				}
				model.addAttribute("settings", passwordSetting);

				return "students/password-change-parent/change-password";
			}
		}

		try {
			SetPasswordResponse.PasswordStatus adPasswordStatus = institutionPersonService.changePassword(form.getUsername(), student.getCpr(), form.getPassword());

			if (ADPasswordService.isCritical(adPasswordStatus)) {
				if (configuration.getStudentAdministration().isIndskolingSpecialEnabled() && Objects.equals(passwordSettingService.findGradeGroup(form.getUsername()), GradeGroup.YOUNGEST)) {
					model.addAttribute("technicalError", true);
					model.addAttribute("passwordWords", passwordSettingService.getPasswordWords());
					model.addAttribute("passwordForm", new PasswordChangeForm(student));

					return "students/password-change-parent/change-password-youngStudent";
				} else {
					PasswordSetting passwordSetting = passwordSettingService.getPasswordSettingsForUsername(form.getUsername());
					if (passwordSetting == null) {
						redirectAttributes.addFlashAttribute("flashError", "Fejl! Kodeordsreglerne tilknyttet denne bruger, kan ikke findes");
						return "redirect:/ui/students/parents/changepassword";
					}

					model.addAttribute("technicalError", true);
					model.addAttribute("settings", passwordSetting);
					model.addAttribute("passwordForm", form);

					return "students/password-change-parent/change-password";
				}
			} else {
				auditLogger.studentPasswordChangeByParent(student.getUsername(), student.getName(), cpr);
			}
		}
		catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
			log.error("Exception while trying to change password on another user", e);

			redirectAttributes.addFlashAttribute("flashError", "Fejl! Der opstod en teknisk fejl");
			return "redirect:/ui/students";
		}

		redirectAttributes.addFlashAttribute("flashSuccess", "Kodeord ændret");

		return "redirect:/elevkode/skiftkodeord";
	}
}
