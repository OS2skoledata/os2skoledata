package dk.digitalidentity.os2skoledata.security;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.config.Constants;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.service.AuditLogger;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import dk.digitalidentity.samlmodule.model.SamlGrantedAuthority;
import dk.digitalidentity.samlmodule.model.SamlLoginPostProcessor;
import dk.digitalidentity.samlmodule.model.TokenUser;

@Component
public class RolePostProcesser implements SamlLoginPostProcessor {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private OS2SkoleDataConfiguration config;

	@Autowired
	private AuditLogger auditLogger;

	@Override
	public void process(TokenUser tokenUser) {
		Set<SamlGrantedAuthority> newAuthorities = new HashSet<>();

		for (Iterator<SamlGrantedAuthority> iterator = tokenUser.getAuthorities().iterator(); iterator.hasNext();) {
			SamlGrantedAuthority grantedAuthority = iterator.next();

			if ("ROLE_admin".equals(grantedAuthority.getAuthority())) {
				newAuthorities.add(new SamlGrantedAuthority(Constants.ADMIN));
			}
		}

		// check if the user is a school employee or external in OS2skoledata
		String username = tokenUser.getUsername();
		List<DBInstitutionPerson> people = institutionPersonService.findByUsernameAndDeletedFalse(username);
		if (people.stream().anyMatch(p -> p.getEmployee() != null || p.getExtern() != null)) {
			newAuthorities.add(new SamlGrantedAuthority(Constants.SCHOOL_EMPLOYEE));
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
