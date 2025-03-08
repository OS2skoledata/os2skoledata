package dk.digitalidentity.os2skoledata.controller.mvc.validator;

import dk.digitalidentity.os2skoledata.controller.mvc.dto.PasswordChangeForm;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.AuditLogger;
import dk.digitalidentity.os2skoledata.service.CprService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.PasswordSettingService;
import dk.digitalidentity.os2skoledata.service.model.enums.ChangePasswordResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
public class PasswordChangeValidator implements Validator {

	@Autowired
	private PasswordSettingService passwordSettingService;

	@Autowired
	private AuditLogger auditLogger;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private SecurityUtil securityUtil;

	@Override
	public boolean supports(Class<?> aClass) {
		return (PasswordChangeForm.class.isAssignableFrom(aClass));
	}

	@Override
	public void validate(Object o, Errors errors) {
		PasswordChangeForm form = (PasswordChangeForm) o;
		
		// Generic checks, not domain specific
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "changePassword.error.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "changePassword.error.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "changePassword.error.required");

		if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
			errors.rejectValue("confirmPassword", "changePassword.error.match");
		}

		ChangePasswordResult validPassword = passwordSettingService.validate(form.getUsername(), form.getPassword());

		if (!validPassword.equals(ChangePasswordResult.OK)) {
			PerformerInfo performerInfo = findPerformerInfo();
			auditLogger.changePasswordFailed(form.getUsername(), form.getPersonName(), performerInfo.username(), performerInfo.name(), validPassword.getMessage());
			errors.rejectValue("password", "changePassword.error.rules");
		}
	}

	private record PerformerInfo(String username, String name) {}
	private PerformerInfo findPerformerInfo() {
		String username = SecurityUtil.getUserId();
		DBInstitutionPerson loggedInPerson = institutionPersonService.findByUsernameAndDeletedFalse(username).stream()
				.filter(p -> p.getEmployee() != null || p.getExtern() != null)
				.findAny().orElse(null);

		String performerUsername = null;
		String performerFirstName = null;
		String performerFamilyName = null;
		if (loggedInPerson != null) {
			performerUsername = loggedInPerson.getUsername();
			performerFirstName = loggedInPerson.getPerson().getAliasFirstName() == null ? loggedInPerson.getPerson().getFirstName() : loggedInPerson.getPerson().getAliasFirstName();
			performerFamilyName = loggedInPerson.getPerson().getAliasFamilyName() == null ? loggedInPerson.getPerson().getFamilyName() : loggedInPerson.getPerson().getAliasFamilyName();
		} else if (StringUtils.hasLength(securityUtil.getCpr())) {
			String cpr = securityUtil.getCpr();
			return new PerformerInfo(null, CprService.safeCprSubstring(cpr));
		}
		return new PerformerInfo(performerUsername, performerFirstName == null ? null : performerFirstName + " " + performerFamilyName);
	}
}