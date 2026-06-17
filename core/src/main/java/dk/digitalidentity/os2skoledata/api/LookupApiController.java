package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.api.model.ErrorDTO;
import dk.digitalidentity.os2skoledata.api.model.PersonLookupDTO;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.security.RequireLookupAPIAccess;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequireLookupAPIAccess
@Tag(name = "Lookup API", description = "Endpoints for looking up persons by CPR, UNI-Login ID or username")
@SecurityRequirement(name = "ApiKeyAuth")
@RestController
@RequestMapping("/api/lookup")
public class LookupApiController {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@GetMapping("/by-cpr")
	@Operation(summary = "Look up a person by CPR number")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Person found",
					content = @Content(schema = @Schema(implementation = PersonLookupDTO.class))),
			@ApiResponse(responseCode = "404", description = "No person found with the given CPR",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	public PersonLookupDTO getByCpr(
			@Parameter(description = "Civil registration number", required = true, example = "1234567890")
			@RequestParam String cpr
	) {
		List<DBInstitutionPerson> people = institutionPersonService.findByPersonCivilRegistrationNumber(cpr);
		return toDto(people);
	}

	@GetMapping("/by-uni-id")
	@Operation(summary = "Look up a person by UNI-Login ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Person found",
					content = @Content(schema = @Schema(implementation = PersonLookupDTO.class))),
			@ApiResponse(responseCode = "404", description = "No person found with the given UNI-Login ID",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	public PersonLookupDTO getByUniId(
			@Parameter(description = "UNI-Login user ID", required = true, example = "10002001fd")
			@RequestParam String uniId
	) {
		List<DBInstitutionPerson> people = institutionPersonService.findByUniLoginUserId(uniId);
		return toDto(people);
	}

	@GetMapping("/by-username")
	@Operation(summary = "Look up a person by username")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Person found",
					content = @Content(schema = @Schema(implementation = PersonLookupDTO.class))),
			@ApiResponse(responseCode = "404", description = "No person found with the given username",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	public PersonLookupDTO getByUsername(
			@Parameter(description = "Username", required = true, example = "anna1234")
			@RequestParam String username
	) {
		List<DBInstitutionPerson> people = institutionPersonService.findByUsername(username);
		return toDto(people);
	}

	private PersonLookupDTO toDto(List<DBInstitutionPerson> people) {
		DBInstitutionPerson person = people.stream()
				.filter(p -> !p.isDeleted())
				.findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No active person found"));

		String cpr = person.getPerson() != null ? person.getPerson().getCivilRegistrationNumber() : null;
		String uniId = person.getUniLogin() != null ? person.getUniLogin().getUserId() : null;
		String username = person.getUsername();

		return new PersonLookupDTO(cpr, uniId, username);
	}
}