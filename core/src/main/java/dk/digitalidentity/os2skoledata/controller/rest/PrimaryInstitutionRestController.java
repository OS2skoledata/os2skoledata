package dk.digitalidentity.os2skoledata.controller.rest;

import dk.digitalidentity.os2skoledata.config.Constants;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.PasswordAdmin;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorOrPasswordAdminRole;
import dk.digitalidentity.os2skoledata.security.SecurityUtil;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.PasswordAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequireAdministratorOrPasswordAdminRole
@RestController
public class PrimaryInstitutionRestController {

	@Autowired
	private InstitutionPersonService institutionPersonService;

	@Autowired
	private InstitutionService institutionService;

	@Autowired
	private PasswordAdminService passwordAdminService;

	private record InstitutionDTO(String institutionName, String institutionNumber, boolean primary) {}
	@GetMapping("/rest/institution/primary/institutions")
	public ResponseEntity<List<InstitutionDTO>> getInstitutionsForUser(@RequestParam String username) {
		try {
			List<DBInstitutionPerson> institutionPersons = institutionPersonService.findByUsername(username);

			if (institutionPersons == null || institutionPersons.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			List<InstitutionDTO> institutions = institutionPersons.stream()
					.map(ip -> new InstitutionDTO(
							ip.getInstitution().getInstitutionName(),
							ip.getInstitution().getInstitutionNumber(),
							ip.isPrimaryInstitution()
					))
					.collect(Collectors.toList());

			return ResponseEntity.ok(institutions);
		} catch (Exception e) {
			log.warn("Error fetching institutions for user: " + username, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private record SavePrimaryInstitutionRequest(String username, String institutionNumber) {}
	@PostMapping("/rest/institution/primary/save")
	public ResponseEntity<Void> savePrimaryInstitution(@RequestBody SavePrimaryInstitutionRequest request) {
		try {
			if (request.username() == null || request.institutionNumber() == null) {
				return ResponseEntity.badRequest().build();
			}

			List<DBInstitutionPerson> institutionPersons = institutionPersonService.findByUsername(request.username());

			if (institutionPersons == null || institutionPersons.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			// Security check: Password admin must have access to at least one of the user's institutions
			if (!SecurityUtil.hasRole(Constants.ADMIN) && SecurityUtil.hasRole(Constants.PASSWORD_ADMIN)) {
				String adminUsername = SecurityUtil.getUserId();
				PasswordAdmin passwordAdmin = passwordAdminService.getByUsername(adminUsername);

				if (passwordAdmin == null) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
				}

				List<String> allowedInstitutionNumbers = passwordAdmin.getInstitutions()
						.stream()
						.map(DBInstitution::getInstitutionNumber)
						.toList();

				boolean hasAccessToUser = institutionPersons.stream()
						.anyMatch(ip -> allowedInstitutionNumbers.contains(ip.getInstitution().getInstitutionNumber()));

				if (!hasAccessToUser) {
					return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
				}
			}

			// Find the institution to set as primary
			DBInstitution targetInstitution = institutionService.findByInstitutionNumber(request.institutionNumber());
			if (targetInstitution == null) {
				return ResponseEntity.notFound().build();
			}

			// Set the selected institution as primary
			DBInstitutionPerson primaryInstitutionPerson = institutionPersons.stream()
					.filter(ip -> ip.getInstitution().getInstitutionNumber().equals(request.institutionNumber()))
					.findFirst()
					.orElse(null);

			if (primaryInstitutionPerson == null) {
				return ResponseEntity.notFound().build();
			}

			institutionPersonService.resetPrimary(primaryInstitutionPerson.getPerson().getCivilRegistrationNumber());
			primaryInstitutionPerson.setPrimaryInstitution(true);

			// Save all changes
			institutionPersonService.save(primaryInstitutionPerson);

			return ResponseEntity.ok().build();
		} catch (Exception e) {
			log.warn("Error saving primary institution for user: " + request.username(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private record UserSearchResultDTO(String username, String firstName, String familyName, int institutionCount) {}

	@GetMapping("/rest/institution/primary/search")
	public ResponseEntity<List<UserSearchResultDTO>> searchUsers(@RequestParam String query) {
		try {
			if (query == null || query.trim().length() < 3) {
				return ResponseEntity.ok(List.of());
			}

			// Search for users with institution connections
			List<DBInstitutionPerson> allMatches = institutionPersonService.searchAllInstitutions(query.trim());

			if (allMatches == null || allMatches.isEmpty()) {
				return ResponseEntity.ok(List.of());
			}

			// Filter based on user role
			List<DBInstitutionPerson> filteredMatches = filterByRole(allMatches);

			if (filteredMatches.isEmpty()) {
				return ResponseEntity.ok(List.of());
			}

			// Group by username and count institutions per user
			Map<String, List<DBInstitutionPerson>> groupedByUser = filteredMatches.stream()
					.collect(Collectors.groupingBy(DBInstitutionPerson::getUsername));

			List<UserSearchResultDTO> results = groupedByUser.entrySet().stream()
					.map(entry -> {
						DBInstitutionPerson firstMatch = entry.getValue().get(0);
						return new UserSearchResultDTO(
								firstMatch.getUsername(),
								firstMatch.getPerson().getFirstName(),
								firstMatch.getPerson().getFamilyName(),
								entry.getValue().size()
						);
					})
					.sorted((a, b) -> {
						// Sort by family name, then first name
						int familyNameCompare = a.familyName().compareToIgnoreCase(b.familyName());
						if (familyNameCompare != 0) {
							return familyNameCompare;
						}
						return a.firstName().compareToIgnoreCase(b.firstName());
					})
					.limit(20)
					.collect(Collectors.toList());

			return ResponseEntity.ok(results);
		} catch (Exception e) {
			log.error("Error searching users with query: " + query, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	private List<DBInstitutionPerson> filterByRole(List<DBInstitutionPerson> matches) {
		if (SecurityUtil.hasRole(Constants.ADMIN)) {
			return matches;
		}

		if (SecurityUtil.hasRole(Constants.PASSWORD_ADMIN)) {
			String username = SecurityUtil.getUserId();
			PasswordAdmin passwordAdmin = passwordAdminService.getByUsername(username);

			if (passwordAdmin == null) {
				return List.of();
			}

			List<String> allowedInstitutionNumbers = passwordAdmin.getInstitutions()
					.stream()
					.map(DBInstitution::getInstitutionNumber)
					.toList();

			return matches.stream()
					.filter(ip -> allowedInstitutionNumbers.contains(ip.getInstitution().getInstitutionNumber()))
					.toList();
		}

		return List.of();
	}
}