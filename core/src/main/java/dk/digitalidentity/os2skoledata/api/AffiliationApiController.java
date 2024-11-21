package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.Affiliation;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBPerson;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class AffiliationApiController {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	private record AffiliationDTO(@NotNull String cpr, LocalDate startDate, LocalDate stopDate, @NotNull String employeeNumber) {}
	@PostMapping("/api/affiliations/fullload")
	public ResponseEntity<?> fullLoad(@Valid @RequestBody List<AffiliationDTO> dtos, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
		}
		if (dtos == null) {
			return new ResponseEntity<>("No affiliations received", HttpStatus.BAD_REQUEST);
		}

		List<DBInstitutionPerson> people = institutionPersonService.findAllNotDeleted();
		List<DBInstitutionPerson> peopleToSave = new ArrayList<>();
		for (DBInstitutionPerson institutionPerson : people) {
			DBPerson person = institutionPerson.getPerson();
			List<AffiliationDTO> affiliationsForPerson = dtos.stream().filter(a -> a.cpr.equals(person.getCivilRegistrationNumber())).collect(Collectors.toList());

			if (affiliationsForPerson.isEmpty() && !person.getAffiliations().isEmpty()) {
				// clear all
				person.getAffiliations().clear();
				peopleToSave.add(institutionPerson);
			} else if (!affiliationsForPerson.isEmpty() && person.getAffiliations().isEmpty()) {
				// add all
				person.getAffiliations().addAll(affiliationsForPerson.stream().map(a -> fromDTO(a, person)).collect(Collectors.toList()));
				peopleToSave.add(institutionPerson);
			} else if (!affiliationsForPerson.isEmpty() && !person.getAffiliations().isEmpty()) {
				// compare, add and remove
				boolean changes = false;

				for (AffiliationDTO forPerson : affiliationsForPerson) {
					Affiliation match = person.getAffiliations().stream().filter(a -> a.getEmployeeNumber().equals(forPerson.employeeNumber())).findAny().orElse(null);
					if (match == null) {
						// add
						person.getAffiliations().add(fromDTO(forPerson, person));
						changes = true;
					} else {
						// update?
						if (!Objects.equals(forPerson.startDate(), match.getStartDate())) {
							match.setStartDate(forPerson.startDate());
							changes = true;
						}
						if (!Objects.equals(forPerson.stopDate(), match.getStopDate())) {
							match.setStopDate(forPerson.stopDate());
							changes = true;
						}
					}
				}

				// remove
				List<String> employeeNumbersToKeep = affiliationsForPerson.stream().map(a -> a.employeeNumber()).collect(Collectors.toList());
				institutionPerson.getPerson().getAffiliations().removeIf(affiliation -> !employeeNumbersToKeep.contains(affiliation.getEmployeeNumber()));

				if (changes) {
					peopleToSave.add(institutionPerson);
				}
			}
		}

		institutionPersonService.saveAll(peopleToSave);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private Affiliation fromDTO(AffiliationDTO affiliationDTO, DBPerson person) {
		Affiliation affiliation = new Affiliation();
		affiliation.setPerson(person);
		affiliation.setEmployeeNumber(affiliationDTO.employeeNumber());
		affiliation.setStartDate(affiliationDTO.startDate());
		affiliation.setStopDate(affiliationDTO.stopDate());
		return affiliation;
	}

}
