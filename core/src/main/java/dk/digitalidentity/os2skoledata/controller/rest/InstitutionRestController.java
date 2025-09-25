package dk.digitalidentity.os2skoledata.controller.rest;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.Setting;
import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.ClientService;
import dk.digitalidentity.os2skoledata.service.GroupService;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequireAdministratorRole
@RestController
public class InstitutionRestController {

	@Autowired
	private SettingService settingService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private GroupService groupService;

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	@PostMapping("/rest/institutions/unlock/{number}")
	public ResponseEntity<?> unlockInstitution(@PathVariable String number) {
		DBInstitution institution = institutionService.findByInstitutionNumber(number);

		if (institution == null) {
			return ResponseEntity.badRequest().build();
		}

		Setting setting = settingService.getByKey(CustomerSetting.LOCKED_INSTITUTION_.toString() + number);
		if (setting != null) {
			setting.setValue("false");
			settingService.save(setting);
		}

		// set perform year change for all clients and reset yearly ids on first unlock of the year
		String yearSetting = settingService.getStringValueByKey(CustomerSetting.PERFORM_YEAR_CHANGE_YEAR.toString());
		int year;
		try {
			year = Integer.parseInt(yearSetting);
		} catch (Exception e) {
			year = 0;
		}

		if (year != LocalDate.now().getYear()) {
			for (Client client : clientService.findAll()) {
				settingService.setValueForKey(CustomerSetting.PERFORM_YEAR_CHANGE_.toString() + client.getId(), true);
			}

			settingService.setValueForKey(CustomerSetting.PERFORM_YEAR_CHANGE_YEAR.toString(), LocalDate.now().getYear() + "");

			groupService.resetAllYearlyIds();
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/rest/institutions/stil/{number}")
	public ResponseEntity<?> approveStilChange(@PathVariable String number) {
		DBInstitution institution = institutionService.findByInstitutionNumber(number);

		if (institution == null) {
			return ResponseEntity.badRequest().build();
		}

		institution.setBypassTooFewPeople(true);
		institution.setChangeProposal(null);
		institutionService.save(institution);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/rest/stil/email")
	public ResponseEntity<?> setSTILContactEmail(@RequestBody String email) {
		settingService.setValueForKey(CustomerSetting.STIL_CHANGE_EMAIL.toString(), email);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/rest/institution/teamadmins/select")
	public ResponseEntity<?> search(@RequestParam Long id, @RequestParam("institution") String institutionNumber) throws Exception {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		if (institution == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		DBInstitutionPerson institutionPerson = institutionPersonService.findById(id);
		if (institutionPerson == null) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		institution.setAzureEmployeeTeamAdmin(institutionPerson);
		institutionService.save(institution);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private record ScoredResult(DBInstitutionPerson person, int score) {}
	record TeamAdminSearchResultDTO(String firstName, String familyName, String institutionName, String username, long id) {}
	@GetMapping("/rest/institution/teamadmins/search")
	public ResponseEntity<?> search(@RequestParam("institution") String institutionNumber, @RequestParam boolean outside, @RequestParam String query) throws Exception {
		List<TeamAdminSearchResultDTO> response = new ArrayList<>();

		List<DBInstitutionPerson> searchResults = new ArrayList<>();
		query = query.replace("(", "");
		query = query.replace(")", "");
		String[] searchTerms = query.toLowerCase().split(" "); // Omform søgeordene til små bogstaver

		if (outside) {
			for (String term : searchTerms) {
				searchResults.addAll(institutionPersonService.searchAllInstitutions(term));
			}
		} else {
			for (String term : searchTerms) {
				searchResults.addAll(institutionPersonService.searchByInstitutionNumber(institutionNumber, term));
			}
		}

		searchResults = searchResults.stream().distinct().collect(Collectors.toList());

		List<ScoredResult> scoredResults = new ArrayList<>();
		boolean scoreThree = false;
		boolean scoreTwo = false;
		for (DBInstitutionPerson person : searchResults) {
			int score = calculateScore(person, searchTerms);

			if (score == 3) {
				scoreThree = true;
			} else if(score == 2) {
				scoreTwo = true;
			}

			scoredResults.add(new ScoredResult(person, score));
		}

		scoredResults.sort((r1, r2) -> Integer.compare(r2.score(), r1.score()));

		List<ScoredResult> topResults;
		if (scoreThree) {
			topResults = scoredResults.stream().filter(s -> s.score == 3).collect(Collectors.toList());
		} else if (scoreTwo) {
			topResults = scoredResults.stream().filter(s -> s.score == 2).collect(Collectors.toList());
		} else {
			topResults = scoredResults.stream().limit(15).collect(Collectors.toList());
		}

		for (ScoredResult scoredResult : topResults) {
			DBInstitutionPerson person = scoredResult.person();
			String username = person.getUsername();
			long id = person.getId();

			response.add(new TeamAdminSearchResultDTO(person.getPerson().getFirstName(), person.getPerson().getFamilyName(), person.getInstitution().getInstitutionName(), username, id));
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private int calculateScore(DBInstitutionPerson person, String[] searchTerms) {
		int score = 0;
		for (String term : searchTerms) {
			if (term.isBlank()) {
				continue;
			}

			if (person.getPerson().getFirstName().toLowerCase().contains(term)) {
				score++;
			}
			if (person.getPerson().getFamilyName().toLowerCase().contains(term)) {
				score++;
			}
			if (person.getUsername().toLowerCase().contains(term)) {
				score++;
			}
		}

		return score;
	}

	public record DeleteResponse(boolean success, String message) {}
	@DeleteMapping("/rest/institutions/nonstil/{id}")
	public ResponseEntity<DeleteResponse> deleteInstitution(@PathVariable Long id) {
		if (!configuration.getNonSTILInstitutions().isEnabled()) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}

		try {
			DBInstitution institution = institutionService.findById(id);

			if (institution == null || !institution.isNonSTILInstitution()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(new DeleteResponse(false, "Institution ikke fundet"));
			}

			// Check if institution has users and mark as deleted
			if (institution.getInstitutionPersons() != null && !institution.getInstitutionPersons().isEmpty()) {
				for (DBInstitutionPerson institutionPerson : institution.getInstitutionPersons()) {
					institutionPerson.setDeleted(true);
					institutionPersonService.save(institutionPerson);
				}
			}

			String institutionName = institution.getInstitutionName();
			institution.setDeleted(true);
			institutionService.save(institution);

			return ResponseEntity.ok(new DeleteResponse(true,
					"Institution '" + institutionName + "' blev slettet"));

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new DeleteResponse(false, "Der opstod en fejl ved sletning af institutionen"));
		}
	}

}
