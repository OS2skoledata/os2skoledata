package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.config.Constants;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.PasswordAdmin;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorOrPasswordAdminRole;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.PasswordAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequireAdministratorOrPasswordAdminRole
@Controller
public class PrimaryInstitutionController {

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private PasswordAdminService passwordAdminService;

	private record UserWithPrimaryInstitutionDTO(String firstname, String surname, String username, String primaryInstitutionName, String primaryInstitutionNumber) {}

	@GetMapping("/ui/primaryinstitutions")
	public String list(Model model) {
		if (SecurityUtil.hasRole(Constants.ADMIN)) {
			model.addAttribute("peopleWithPrimaryInstitution", institutionPersonService.findByPrimaryTrue()
					.stream()
					.map(i -> new UserWithPrimaryInstitutionDTO(
							i.getPerson().getFirstName(),
							i.getPerson().getFamilyName(),
							i.getUsername(),
							i.getInstitution().getInstitutionName(),
							i.getInstitution().getInstitutionNumber()
					))
					.collect(Collectors.toList()));
		} else if (SecurityUtil.hasRole(Constants.PASSWORD_ADMIN)) {
			String username = SecurityUtil.getUserId();
			PasswordAdmin passwordAdmin = passwordAdminService.getByUsername(username);
			if (passwordAdmin != null) {
				List<String> passwordAdminInstitutionNumbers = passwordAdmin.getInstitutions()
						.stream()
						.map(DBInstitution::getInstitutionNumber)
						.toList();

				List<DBInstitutionPerson> allInstitutionPeople = institutionPersonService.findAllNotDeleted();

				// Find all usernames that have at least one institution connection the password admin has access to
				Set<String> allowedUsernames = allInstitutionPeople.stream()
						.filter(ip -> passwordAdminInstitutionNumbers.contains(ip.getInstitution().getInstitutionNumber()))
						.map(DBInstitutionPerson::getUsername)
						.collect(Collectors.toSet());

				// Show primary institutions for these users (from the same dataset)
				model.addAttribute("peopleWithPrimaryInstitution", allInstitutionPeople.stream()
						.filter(ip -> ip.isPrimaryInstitution() && allowedUsernames.contains(ip.getUsername()))
						.map(i -> new UserWithPrimaryInstitutionDTO(
								i.getPerson().getFirstName(),
								i.getPerson().getFamilyName(),
								i.getUsername(),
								i.getInstitution().getInstitutionName(),
								i.getInstitution().getInstitutionNumber()
						))
						.collect(Collectors.toList()));
			}
		}

		return "institutions/primary";
	}
}
