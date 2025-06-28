package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.framework.ad.service.model.SetPasswordResponse;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.controller.mvc.dto.PasswordChangeForm;
import dk.digitalidentity.os2skoledata.controller.mvc.validator.PasswordChangeValidator;
import dk.digitalidentity.os2skoledata.controller.mvc.xlsview.StudentListXlsxView;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.PasswordSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import dk.digitalidentity.os2skoledata.security.RequireSchoolEmployeeOrPasswordAdminRole;
import dk.digitalidentity.os2skoledata.security.RequireSchoolEmployeeRole;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.ADPasswordService;
import dk.digitalidentity.os2skoledata.service.AuditLogger;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.PasswordSettingService;
import dk.digitalidentity.os2skoledata.service.model.PrintGroupDTO;
import dk.digitalidentity.os2skoledata.service.model.StudentPasswordChangeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequireSchoolEmployeeOrPasswordAdminRole
@Controller
public class StudentController {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private PasswordChangeValidator passwordChangeFormValidator;

	@Autowired
	private PasswordSettingService passwordSettingService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@Autowired
	private AuditLogger auditLogger;

	@Autowired
	private GroupService groupService;

	@Autowired
	private MessageSource messageSource;

	@InitBinder("passwordForm")
	public void initClientBinder(WebDataBinder binder) {
		binder.setValidator(passwordChangeFormValidator);
	}

	@GetMapping("/ui/students")
	public String list(Model model) {
		if (!configuration.getStudentAdministration().isEnabled() || configuration.getStudentAdministration().isClassListsOnly()) {
			return "redirect:/error";
		}

		model.addAttribute("people", institutionPersonService.getStudentsThatPasswordCanBeChangedOnByPerson());

		return "students/list";
	}

	@RequireSchoolEmployeeRole
	@GetMapping("/ui/students/groups")
	public String listGroups(Model model) {
		if (!configuration.getStudentAdministration().isEnabled()) {
			return "redirect:/error";
		}

		model.addAttribute("groups", groupService.getClassesForPrint());

		return "students/groups/list";
	}

	@GetMapping("/ui/students/{username}/changepassword")
	public String changePassword(Model model, RedirectAttributes redirectAttributes, @PathVariable("username") String username) {
		if (!configuration.getStudentAdministration().isEnabled() || configuration.getStudentAdministration().isClassListsOnly()) {
			return "redirect:/error";
		}

		StudentPasswordChangeDTO student = institutionPersonService.getStudentIfAllowedPasswordChange(username);
		if (student == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Det er ikke tilladt at skifte kodeord på denne bruger");
			return "redirect:/ui/students";
		}

		PasswordSetting passwordSetting = passwordSettingService.getPasswordSettingsForUsername(username);
		if (passwordSetting == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Kodeordsreglerne tilknyttet denne bruger, kan ikke findes");
			return "redirect:/ui/students";
		}

		if (configuration.getStudentAdministration().isIndskolingSpecialEnabled() && Objects.equals(passwordSettingService.findGradeGroup(username), GradeGroup.YOUNGEST)) {
			model.addAttribute("passwordWords", passwordSettingService.getPasswordWords());
			model.addAttribute("passwordForm", new PasswordChangeForm(student));

			return "students/change-password-youngStudent";
		} else {
			model.addAttribute("settings", passwordSetting);
			model.addAttribute("passwordForm", new PasswordChangeForm(student));

			return "students/change-password";
		}
	}

	@PostMapping("/students/changepassword")
	public String changePassword(Model model, RedirectAttributes redirectAttributes, @Valid @ModelAttribute("passwordForm") PasswordChangeForm form, BindingResult bindingResult) {
		if (!configuration.getStudentAdministration().isEnabled() || configuration.getStudentAdministration().isClassListsOnly()) {
			return "redirect:/error";
		}

		StudentPasswordChangeDTO student = institutionPersonService.getStudentIfAllowedPasswordChange(form.getUsername());
		if (student == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Det er ikke tilladt at skifte kodeord på denne bruger");
			return "redirect:/ui/students";
		}

		PasswordSetting passwordSetting = passwordSettingService.getPasswordSettingsForUsername(form.getUsername());
		if (passwordSetting == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Kodeordsreglerne tilknyttet denne bruger, kan ikke findes");
			return "redirect:/ui/students";
		}

		// Check for password errors
		if (bindingResult.hasErrors()) {
			model.addAttribute("settings", passwordSetting);
			model.addAttribute("passwordForm", form);

			return "students/change-password";
		}

		try {
			SetPasswordResponse.PasswordStatus adPasswordStatus = institutionPersonService.changePassword(form.getUsername(), student.getCpr(), form.getPassword());

			if (ADPasswordService.isCritical(adPasswordStatus)) {
				model.addAttribute("technicalError", true);
				model.addAttribute("settings", passwordSetting);
				model.addAttribute("passwordForm", form);

				return "students/change-password";
			} else {
				auditLogger.studentPasswordChangeByEmployee(student.getUsername(), student.getName(), SecurityUtil.getUserId(), null);
			}
		}
		catch (NoSuchPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException e) {
			log.error("Exception while trying to change password on another user", e);

			redirectAttributes.addFlashAttribute("flashError", "Fejl! Der opstod en teknisk fejl");
			return "redirect:/ui/students";
		}

		redirectAttributes.addFlashAttribute("flashSuccess", "Kodeord ændret");

		return "redirect:/ui/students";
	}

	@RequireSchoolEmployeeRole
	@GetMapping("/students/groups/{id}/print")
	public String getClassStudentPrint(Model model, RedirectAttributes redirectAttributes, @PathVariable("id") long id, @RequestParam("withPassword") boolean withPassword) {
		if (!configuration.getStudentAdministration().isEnabled()) {
			return "redirect:/error";
		}

		DBGroup group = groupService.findById(id);
		if (group == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Klassen kan ikke findes");
			return "redirect:/ui/students/groups";
		}

		PrintGroupDTO printGroupDTO = groupService.getClassesForPrint().stream().filter(c -> c.getId() == group.getId()).findAny().orElse(null);
		if (printGroupDTO == null) {
			redirectAttributes.addFlashAttribute("flashError", "Fejl! Du har ikke adgang til denne klasse");
			return "redirect:/ui/students/groups";
		}

		if (withPassword && !printGroupDTO.isCanPrintPassword()) {
			withPassword = false;
		}

		if (configuration.getStudentAdministration().isClassListsOnly()) {
			withPassword = false;
		}

		model.addAttribute("withPassword", withPassword);
		model.addAttribute("students", institutionPersonService.getStudentsInClassForPrint(group, withPassword));
		model.addAttribute("className", printGroupDTO.getGroupName());

		return "students/groups/print_classes_students";
	}

	@RequireSchoolEmployeeRole
	@GetMapping("/students/groups/{id}/csv")
	public ModelAndView getClassStudentCsv(final HttpServletResponse response, Locale loc, @PathVariable("id") long id, @RequestParam("withPassword") boolean withPassword) {
		if (!configuration.getStudentAdministration().isEnabled()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		DBGroup group = groupService.findById(id);
		if (group == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		PrintGroupDTO printGroupDTO = groupService.getClassesForPrint().stream().filter(c -> c.getId() == group.getId()).findAny().orElse(null);
		if (printGroupDTO == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}

		if (withPassword && !printGroupDTO.isCanPrintPassword()) {
			withPassword = false;
		}

		if (configuration.getStudentAdministration().isClassListsOnly()) {
			withPassword = false;
		}

		final Map<String, Object> model = new HashMap<>();
		model.put("withPassword", withPassword);
		model.put("students", institutionPersonService.getStudentsInClassForPrint(group, withPassword));
		model.put("groupName", printGroupDTO.getGroupName());
		model.put("locale", loc);
		model.put("messagesBundle", messageSource);

		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + printGroupDTO.getGroupName() + ".xlsx\"");
		return new ModelAndView(new StudentListXlsxView(), model);
	}
}
