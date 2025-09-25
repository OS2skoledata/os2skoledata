package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.InstitutionDao;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.InstitutionChangeProposal;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequireAdministratorRole
@Controller
public class InstitutionController {

	@Autowired
	private SettingService settingService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;
	@Autowired
	private InstitutionDao institutionDao;

	record ListInstitutionDTO(String name, String number, boolean locked, boolean unlockPossible, boolean hasTooFewPeople, String tooFewPeopleErrorMessage) {}
	@GetMapping("/ui/institutions")
	public String list(Model model) {
		List<DBInstitution> institutions = institutionService.findAll();
		Setting globalSetting = settingService.getByKey(CustomerSetting.GLOBAL_SCHOOL_YEAR.toString());
		String globalSchoolYear = globalSetting == null ? "-1" : globalSetting.getValue();

		List<ListInstitutionDTO> dtos = new ArrayList<>();
		for (DBInstitution institution : institutions) {
			if (institution.isNonSTILInstitution()) {
				continue;
			}
			Setting importYearSetting = settingService.getByKey(CustomerSetting.IMPORT_SOURCE_SCHOOL_YEAR_.toString() + institution.getInstitutionNumber());
			String importYear = importYearSetting == null ? null : importYearSetting.getValue();
			dtos.add(new ListInstitutionDTO(
					institution.getInstitutionName(),
					institution.getInstitutionNumber(),
					settingService.getBooleanValueByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + institution.getInstitutionNumber()),
					Objects.equals(globalSchoolYear, importYear),
					institution.getChangeProposal() != null && !institution.isBypassTooFewPeople(),
					institution.getChangeProposal() != null ? institution.getChangeProposal().getTooFewPeopleErrorMessage() : null));
		}

		model.addAttribute("institutions", dtos);
		model.addAttribute("stilContactEmail", settingService.getStringValueByKey(CustomerSetting.STIL_CHANGE_EMAIL));

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

	record InstitutionChangesDTO(String institutionName, String institutionNumber, String errorMessage, List<PersonChangeDTO> personChanges) {}
	record PersonChangeDTO( String changeType, String firstName, String familyName, String uniLoginUserId, String personType) {}
	@GetMapping("/ui/institutions/{institutionNumber}/changes")
	public String viewChanges(@PathVariable String institutionNumber, Model model) {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);

		if (institution == null || institution.getChangeProposal() == null) {
			// Redirect back to institutions list if no changes found
			return "redirect:/ui/institutions";
		}

		InstitutionChangeProposal changeProposal = institution.getChangeProposal();

		List<PersonChangeDTO> personChanges = changeProposal.getProposedPersonChanges().stream()
				.map(pc -> new PersonChangeDTO(
						pc.getChangeType().toString(),
						pc.getFirstName(),
						pc.getFamilyName(),
						pc.getUniLoginUserId(),
						pc.getPersonType()
				))
				.collect(Collectors.toList());

		InstitutionChangesDTO dto = new InstitutionChangesDTO(
				institution.getInstitutionName(),
				institution.getInstitutionNumber(),
				changeProposal.getTooFewPeopleErrorMessage(),
				personChanges
		);

		model.addAttribute("changes", dto);

		return "institutions/changes";
	}

	@GetMapping("/ui/institutions/nonstil")
	public String listInstitutions(Model model) {
		if (!configuration.getNonSTILInstitutions().isEnabled()) {
			return "redirect:/error";
		}

		List<DBInstitution> institutions = institutionService.findAllNonStilInstitutions();
		model.addAttribute("institutions", institutions);
		model.addAttribute("institutionTypes", InstitutionType.values());

		// Add empty form object if not already present (from validation errors)
		if (!model.containsAttribute("institutionForm")) {
			model.addAttribute("institutionForm", new NonStilInstitutionForm("", "", "", null));
		}

		return "nonstil-institutions/list";
	}

	private record NonStilInstitutionForm(
			@NotBlank(message = "Institutionens navn er påkrævet")
			@Size(max = 255, message = "Institutionens navn må ikke være længere end 255 tegn")
			String institutionName,

			@NotBlank(message = "Institutionsnummer er påkrævet")
			@Size(max = 255, message = "Institutionsnummer må ikke være længere end 255 tegn")
			String institutionNumber,

			@Size(max = 255, message = "Forkortelse må ikke være længere end 255 tegn")
			String abbreviation,

			@NotNull(message = "Type er påkrævet")
			InstitutionType type
	) {}

	@PostMapping("/ui/institutions/nonstil/create")
	public String createInstitution(@Valid @ModelAttribute("institutionForm") NonStilInstitutionForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (!configuration.getNonSTILInstitutions().isEnabled()) {
			return "redirect:/error";
		}

		// Check if institution number already exists
		if (institutionService.findByInstitutionNumber(form.institutionNumber()) != null) {
			bindingResult.rejectValue("institutionNumber", "error.institutionNumber.exists",
					"En institution med dette nummer eksisterer allerede");
		}

		// Check if institution abbreviation already exists (if not empty)
		if (form.abbreviation() != null && !form.abbreviation().trim().isEmpty() &&
				institutionService.findByAbbreviation(form.abbreviation()) != null) {
			bindingResult.rejectValue("abbreviation", "error.abbreviation.exists",
					"En institution med denne forkortelse eksisterer allerede");
		}

		if (bindingResult.hasErrors()) {
			// Return to list with error data for modal
			model.addAttribute("institutions", institutionService.findAllNonStilInstitutions());
			model.addAttribute("institutionTypes", InstitutionType.values());
			model.addAttribute("validationErrors", bindingResult.getAllErrors());
			model.addAttribute("formData", form);
			return "nonstil-institutions/list";
		}

		try {
			DBInstitution institution = new DBInstitution();
			institution.setInstitutionName(form.institutionName());
			institution.setInstitutionNumber(form.institutionNumber());
			institution.setAbbreviation(form.abbreviation());
			institution.setType(form.type());
			institution.setNonSTILInstitution(true);
			institutionDao.save(institution);

			redirectAttributes.addFlashAttribute("successMessage",
					"Institution '" + institution.getInstitutionName() + "' blev oprettet");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Der opstod en fejl ved oprettelse af institutionen");
		}

		return "redirect:/ui/institutions/nonstil";
	}

	@PostMapping("/ui/institutions/nonstil/{id}/edit")
	public String updateInstitution(@PathVariable Long id,
			@Valid @ModelAttribute("institutionForm") NonStilInstitutionForm form,
			BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (!configuration.getNonSTILInstitutions().isEnabled()) {
			return "redirect:/error";
		}

		// Get the institution to edit
		DBInstitution institution = institutionService.findById(id);
		if (institution == null || !institution.isNonSTILInstitution()) {
			redirectAttributes.addFlashAttribute("errorMessage", "Institutionen blev ikke fundet");
			return "redirect:/ui/institutions/nonstil";
		}

		// Check if abbreviation already exists (excluding current institution)
		if (form.abbreviation() != null && !form.abbreviation().trim().isEmpty()) {
			DBInstitution existingWithAbbr = institutionService.findByAbbreviation(form.abbreviation());
			if (existingWithAbbr != null && existingWithAbbr.getId() != id) {
				bindingResult.rejectValue("abbreviation", "error.abbreviation.exists",
						"En institution med denne forkortelse eksisterer allerede");
			}
		}

		if (bindingResult.hasErrors()) {
			// Return to list with error data for modal
			model.addAttribute("institutions", institutionService.findAllNonStilInstitutions());
			model.addAttribute("institutionTypes", InstitutionType.values());
			model.addAttribute("validationErrors", bindingResult.getAllErrors());
			model.addAttribute("formData", form);
			model.addAttribute("editId", id);
			return "nonstil-institutions/list";
		}

		try {
			institution.setInstitutionName(form.institutionName());
			institution.setAbbreviation(form.abbreviation());
			institutionDao.save(institution);

			redirectAttributes.addFlashAttribute("successMessage",
					"Institution '" + institution.getInstitutionName() + "' blev opdateret");

		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Der opstod en fejl ved opdatering af institutionen");
		}

		return "redirect:/ui/institutions/nonstil";
	}

}
