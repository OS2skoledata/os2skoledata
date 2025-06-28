package dk.digitalidentity.os2skoledata.security;

import dk.digitalidentity.os2skoledata.config.Constants;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.PasswordAdmin;
import dk.digitalidentity.os2skoledata.service.AuditLogger;
import dk.digitalidentity.os2skoledata.service.ClassroomAdminService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.PasswordAdminService;
import dk.digitalidentity.samlmodule.model.SamlGrantedAuthority;
import dk.digitalidentity.samlmodule.model.SamlLoginPostProcessor;
import dk.digitalidentity.samlmodule.model.TokenUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
public class RolePostProcesser implements SamlLoginPostProcessor {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private OS2SkoleDataConfiguration config;

	@Autowired
	private AuditLogger auditLogger;

	@Autowired
	private ClassroomAdminService classroomAdminService;

	@Autowired
	private PasswordAdminService passwordAdminService;

	@Override
	public void process(TokenUser tokenUser) {
		Set<SamlGrantedAuthority> newAuthorities = new HashSet<>();

		boolean passwordAdminPossible = false;
		for (Iterator<SamlGrantedAuthority> iterator = tokenUser.getAuthorities().iterator(); iterator.hasNext();) {
			SamlGrantedAuthority grantedAuthority = iterator.next();

			if ("ROLE_admin".equals(grantedAuthority.getAuthority())) {
				newAuthorities.add(new SamlGrantedAuthority(Constants.ADMIN));
			}

			if ("ROLE_password_admin".equals(grantedAuthority.getAuthority())) {
				passwordAdminPossible = true;
			}
		}

		// check if the user is a school employee or external in OS2skoledata
		String username = tokenUser.getUsername();
		List<DBInstitutionPerson> people = institutionPersonService.findByUsernameAndDeletedFalse(username);
		if (people.stream().anyMatch(p -> p.getEmployee() != null || p.getExtern() != null)) {
			newAuthorities.add(new SamlGrantedAuthority(Constants.SCHOOL_EMPLOYEE));
			passwordAdminPossible = true;
		}

		// check if the user is a Google Classroom admin
		if (classroomAdminService.isClassroomAdmin(username)) {
			newAuthorities.add(new SamlGrantedAuthority(Constants.GOOGLE_CLASSROOM_ADMIN));
		}

		// check if the user is a password admin
		if (passwordAdminPossible) {
			if (passwordAdminService.getByUsername(username) != null) {
				newAuthorities.add(new SamlGrantedAuthority(Constants.PASSWORD_ADMIN));
			}
		}

		// if none of the above roles, check if user is parent
		if (config.getStudentAdministration().isParentPasswordChangeEnabled() && newAuthorities.isEmpty() && tokenUser.getAttributes().containsKey(Constants.CPR_ATTRIBUTE_KEY)) {
			newAuthorities.add(new SamlGrantedAuthority(Constants.PARENT));
			auditLogger.loginStudentPasswordChange((String) tokenUser.getAttributes().get(Constants.CPR_ATTRIBUTE_KEY));
		}

		if (config.isDev()) {
			newAuthorities.add(new SamlGrantedAuthority(Constants.ADMIN));
			newAuthorities.add(new SamlGrantedAuthority(Constants.SCHOOL_EMPLOYEE));
		}

		if (newAuthorities.isEmpty()) {
			throw new UsernameNotFoundException("Ingen tildelte roller");
		}

		tokenUser.setAuthorities(newAuthorities);
	}
}
