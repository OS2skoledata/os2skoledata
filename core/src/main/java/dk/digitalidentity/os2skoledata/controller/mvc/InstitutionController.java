package dk.digitalidentity.os2skoledata.controller.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.SettingService;

@RequireAdministratorRole
@Controller
public class InstitutionController {

	@Autowired
	private SettingService settingService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	record ListInstitutionDTO(String name, String number, boolean locked, boolean unlockPossible) {}
	@GetMapping("/ui/institutions")
	public String list(Model model) {
		List<DBInstitution> institutions = institutionService.findAll();
		Setting globalSetting = settingService.getByKey(CustomerSetting.GLOBAL_SCHOOL_YEAR.toString());
		String globalSchoolYear = globalSetting == null ? "-1" : globalSetting.getValue();

		List<ListInstitutionDTO> dtos = new ArrayList<>();
		for (DBInstitution institution : institutions) {
			Setting importYearSetting = settingService.getByKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institution.getInstitutionNumber());
			String importYear = importYearSetting == null ? null : importYearSetting.getValue();
			dtos.add(new ListInstitutionDTO(institution.getInstitutionName(), institution.getInstitutionNumber(), settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institution.getInstitutionNumber()), Objects.equals(globalSchoolYear, importYear)));
		}

		model.addAttribute("institutions", dtos);

		return "institutions/list";
	}

	@GetMapping("/ui/institutions/teamadmins")
	public String listSchools(Model model) {
		if (!configuration.getTeamAdminAdministration().isEnabled()) {
			return "redirect:/error";
		}

		List<DBInstitution> institutions = institutionService.findAll().stream().filter(i -> i.getType().equals(InstitutionType.SCHOOL)).collect(Collectors.toList());
		model.addAttribute("institutions", institutions);

		return "institutions/teamadmins";
	}
}
