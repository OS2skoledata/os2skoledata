package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PrimaryInstitutionApiController {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private InstitutionService institutionService;

	record InstitutionDTO(String primaryInstitutionName, String primaryInstitutionNumber) {}
	record LookUpDTO(String cpr) {}
	@PostMapping("/api/primaryinstitution/lookup")
	public InstitutionDTO getPrimaryInstitutionFor(@RequestBody LookUpDTO dto) {
		List<DBInstitutionPerson> people = institutionPersonService.findByPersonCivilRegistrationNumber(dto.cpr);
		DBInstitutionPerson primaryPerson = people.stream().filter(p -> p.isPrimaryInstitution()).findFirst().orElse(null);
		return new InstitutionDTO(primaryPerson == null ? null : primaryPerson.getInstitution().getInstitutionName(), primaryPerson == null ? null : primaryPerson.getInstitution().getInstitutionNumber());
	}

	record SetPrimaryInstitution(String cpr, String institutionNumber) {}
	@PostMapping("/api/primaryinstitution")
	public ResponseEntity<?> setPrimaryInstitutionFor(@RequestBody SetPrimaryInstitution dto) {
		DBInstitution institution = institutionService.findByInstitutionNumber(dto.institutionNumber);
		if (institution == null) {
			return ResponseEntity.notFound().build();
		}
		List<DBInstitutionPerson> people = institutionPersonService.findByPersonCivilRegistrationNumberAndInstitution(dto.cpr, institution);
		if (people.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		institutionPersonService.resetPrimary(dto.cpr);
		DBInstitutionPerson person = people.get(0);
		person.setPrimaryInstitution(true);
		institutionPersonService.save(person);

		return ResponseEntity.ok().build();
	}
}
