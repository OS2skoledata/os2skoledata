package dk.digitalidentity.os2skoledata.api;

import dk.digitalidentity.os2skoledata.api.model.EmployeeDTO;
import dk.digitalidentity.os2skoledata.api.model.EmployeeImportDTO;
import dk.digitalidentity.os2skoledata.api.model.ErrorDTO;
import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportRole;
import dk.digitalidentity.os2skoledata.api.model.enums.PossibleImportSubrole;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.security.RequireImportAPIAccess;
import dk.digitalidentity.os2skoledata.service.InstitutionPersonService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.api.ImportApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Slf4j
@RequireImportAPIAccess
@Tag(name = "Import API", description = "Endpoints for creating, deleting, updating and looking up employees and externals")
@SecurityRequirement(name = "ApiKeyAuth")
@RestController
@RequestMapping("/api/import")
public class ImportApiController {

	@Autowired
	private InstitutionPersonService institutionPersonService;
	@Autowired
	private InstitutionService institutionService;
	@Autowired
	private OS2SkoleDataConfiguration os2SkoleDataConfiguration;
	@Autowired
	private ImportApiService importApiService;

	@GetMapping
	@Operation(
			summary = "Returns all employees/externals. It is possible to filter on institution",
			description = "Retrieves a list of employees/externals and optionally filters by institution number."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Employees found (can be an empty list)",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDTO.class)))),
			@ApiResponse(responseCode = "404", description = "Institution not found",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	public List<EmployeeDTO> getEmployees(
			@Parameter(
					name = "institutionNumber",
					description = "Optional institution number to filter employees/externals by",
					required = false,
					example = "10001"
			)
			@RequestParam(name = "institutionNumber", required = false) String institutionNumber
	) {
		List<DBInstitutionPerson> people;
		if (!isNullOrBlank(institutionNumber)) {
			DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
			if (institution == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given institution does not exist");
			}

			people = institutionPersonService.findEmployeeOrExternalByInstitution(institution);
		} else {
			people = institutionPersonService.findAllEmployeesAndExternalsIncludingDeleted();
		}

		if (people.isEmpty()) {
			return List.of(); // return empty list if no employees found
		}

		return people.stream().map(importApiService::toDto).toList();
	}

	private record LookupDTO(String cpr, String institutionNumber) {}
	@PostMapping("/lookup")
	@Operation(
			summary = "Look up employee(s)",
			description = "Retrieves a list of employees based on civil registration number (CPR) and optionally institution number (LookupDTO)."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Employee(s) found",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDTO.class)))),
			@ApiResponse(responseCode = "404", description = "No employee(s) found or institution does not exist",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Request containing CPR and optional institution number",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = LookupDTO.class),
					examples = @ExampleObject(
							name = "Lookup example",
							summary = "A valid lookup employee request",
							value = """
						{
						  "cpr": "1234567890",
						  "institutionNumber": "10001"
						}
						"""
					)
			)
	)
	public List<EmployeeDTO> getEmployee(
			@org.springframework.web.bind.annotation.RequestBody LookupDTO request
	) {
		List<DBInstitutionPerson> people;
		String extraLog = "";
		if (!isNullOrBlank(request.institutionNumber)) {
			DBInstitution institution = institutionService.findByInstitutionNumber(request.institutionNumber());
			if (institution == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given institution does not exist");
			}

			people = institutionPersonService.findByPersonCivilRegistrationNumberAndInstitution(request.cpr(), institution);
			extraLog = " in the institution with institution number " + request.institutionNumber();
		} else {
			people = institutionPersonService.findByPersonCivilRegistrationNumber(request.cpr());
		}

		if (people.isEmpty()) {
			// Overvej om det giver mere mening at returnere en tom liste, når der alligevel bruges liste i retur værdi
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Failed to find people with the given civil registration number" + extraLog);
		}

		return people.stream().map(p -> importApiService.toDto(p)).toList();
	}

	@Transactional
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(
			summary = "Create a new employee",
			description = "Creates a new employee in the system based on the provided import data (EmployeeImportDTO). The following fields are required: CPR, institutionNumber, firstName, familyName, mainRole, roles and source."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Employee successfully created",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad request due to missing or invalid fields",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "404", description = "Institution not found",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "409", description = "Conflict - Employee with the same CPR already exists in the institution",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Request containing employee data including CPR, institution number, roles, and optional username and UNI ID.",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = EmployeeImportDTO.class),
					examples = @ExampleObject(
							name = "Create example",
							summary = "A valid employee creation request",
							value = """
						{
						  "firstName": "Anna",
						  "familyName": "Andersen",
						  "cpr": "1234567890",
						  "institutionNumber": "10001",
						  "mainRole": "ANSAT",
						  "roles": ["LÆRER", "LEDER"],
						  "reservedUsername": "anna1234",
						  "uniID": "1000009642",
						  "source": "local",
						  "groupIDs": ["2347032", "2347033"],
						  "primary": false,
						  "nameProtected": null,
						  "aliasFirstName": null,
						  "aliasFamilyName": null
						}
						"""
					)
			)
	)
	public EmployeeDTO createEmployee(
			@org.springframework.web.bind.annotation.RequestBody @Valid EmployeeImportDTO importDTO
	) {
		DBInstitution institution = institutionService.findByInstitutionNumber(importDTO.getInstitutionNumber());
		if (institution == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given institution does not exist");
		}

		List<DBInstitutionPerson> people = institutionPersonService.findByPersonCivilRegistrationNumberAndInstitution(importDTO.getCpr(), institution);
		boolean anyActive = people.stream().anyMatch(p -> !p.isDeleted());
		if (anyActive) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Person with the given cpr in this institution already exists");
		}

		if (isNullOrBlank(importDTO.getReservedUsername()) && isNullOrBlank(importDTO.getUniID())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reservedUsername or uniID must have a value");
		}

		if (importDTO.getMainRole().equals(PossibleImportRole.EKSTERN) && importDTO.getRoles().size() > 1) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Externals are only allowed to have 1 role");
		}

		if (importDTO.getMainRole().equals(PossibleImportRole.EKSTERN) && containsNonExternalRoles(importDTO)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Externals are only allowed to have the following roles: PRAKTIKANT, EKSTERN");
		}
		if (importDTO.getMainRole().equals(PossibleImportRole.ANSAT) && containsNonEmployeeRoles(importDTO)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Externals are only allowed to have the following roles: LÆRER, PÆDAGOG, VIKAR, LEDER, LEDELSE, TAP, KONSULENT");
		}

		EmployeeDTO result;
		DBInstitutionPerson deletedPersonToReactivate = people.stream().filter(p -> p.isDeleted()).findFirst().orElse(null);
		if (deletedPersonToReactivate != null) {
			result = importApiService.handlePatchUpdate(deletedPersonToReactivate, importDTO);
		} else {
			result = importApiService.handleCreate(importDTO, institution, this);
		}
		return result;
	}

	@Transactional
	@PatchMapping
	@Operation(
			summary = "Patch an existing employee",
			description = "Patch an existing employee's information based on the provided data (EmployeeImportDTO). If no active employee is found, a deleted employee may be reactivated. If a field is left out or null the field won't be updated."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Employee successfully updated",
					content = @Content(mediaType = "application/json", schema = @Schema(implementation = EmployeeDTO.class))),
			@ApiResponse(responseCode = "400", description = "Bad request due to invalid or conflicting data",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "404", description = "Employee or institution not found",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class))),
			@ApiResponse(responseCode = "409", description = "Conflict - Invalid role changes",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Request containing employee data to update.",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = EmployeeImportDTO.class),
					examples = @ExampleObject(
							name = "Patch example",
							summary = "A valid employee patch request",
							value = """
				{
				  "firstName": null,
				  "familyName": "Kjær Andersen",
				  "cpr": "1234567890",
				  "institutionNumber": "10001",
				  "mainRole": null,
				  "roles": ["LÆRER"],
				  "reservedUsername": null,
				  "uniID": null,
				  "source": null,
				  "groupIDs": null,
				  "primary": null,
				  "nameProtected": null,
				  "aliasFirstName": null,
				  "aliasFamilyName": null
				}
				"""
					)
			)
	)
	public EmployeeDTO patchEmployee(
			@org.springframework.web.bind.annotation.RequestBody EmployeeImportDTO importDTO
	) {
		DBInstitution institution = institutionService.findByInstitutionNumber(importDTO.getInstitutionNumber());
		if (institution == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given institution does not exist");
		}

		DBInstitutionPerson toUpdate = null;
		List<DBInstitutionPerson> people = institutionPersonService.findByPersonCivilRegistrationNumberAndInstitution(importDTO.getCpr(), institution);
		toUpdate = people.stream().filter(p -> !p.isDeleted()).findFirst().orElse(null);
		if (toUpdate == null) {
			toUpdate = people.stream().filter(DBInstitutionPerson::isDeleted).findFirst().orElse(null);

			if (toUpdate == null) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active employee with the given civil registration number in the institution with institution number " + importDTO.getInstitutionNumber() + " was found");
			}
		}

		if (importDTO.getFirstName() != null && importDTO.getFirstName().isBlank() ||
				importDTO.getFamilyName() != null && importDTO.getFamilyName().isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "First and last name must not be blank");
		}

		if (importDTO.getRoles() != null && importDTO.getRoles().isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Roles must not be empty");
		}

		if ((importDTO.getMainRole() != null && importDTO.getRoles() != null && importDTO.getMainRole().equals(PossibleImportRole.EKSTERN) && containsNonExternalRoles(importDTO)) ||
				(importDTO.getMainRole() == null && toUpdate.getExtern() != null && containsNonExternalRoles(importDTO))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Externals are only allowed to have the following roles: PRAKTIKANT, EKSTERN");
		} else if ((importDTO.getMainRole() != null && importDTO.getRoles() != null && importDTO.getMainRole().equals(PossibleImportRole.ANSAT) && containsNonEmployeeRoles(importDTO)) ||
				(importDTO.getMainRole() == null && toUpdate.getEmployee() != null && containsNonEmployeeRoles(importDTO))) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Externals are only allowed to have the following roles: LÆRER, PÆDAGOG, VIKAR, LEDER, LEDELSE, TAP, KONSULENT");
		}

		return importApiService.handlePatchUpdate(toUpdate, importDTO);
	}

	@Transactional
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping
	@Operation(
			summary = "Delete employee(s)",
			description = "Soft deletes all non-deleted employees matching the given CPR and institution number. Deleted employees are marked as inactive but retained in the database."
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Employee(s) successfully deleted"),
			@ApiResponse(responseCode = "404", description = "Institution not found or no matching employees found",
					content = @Content(schema = @Schema(implementation = ErrorDTO.class)))
	})
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Request (LookupDTO) containing CPR and institution number to identify the employee(s) to delete.",
			required = true,
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = LookupDTO.class),
					examples = @ExampleObject(
							name = "Delete example",
							summary = "A valid delete employee request",
							value = """
				{
				  "cpr": "1234567890",
				  "institutionNumber": "10001"
				}
				"""
					)
			)
	)
	public void deleteEmployee(
			@org.springframework.web.bind.annotation.RequestBody LookupDTO request
	) {
		DBInstitution institution = institutionService.findByInstitutionNumber(request.institutionNumber());
		if (institution == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The given institution does not exist");
		}

		List<DBInstitutionPerson> people = institutionPersonService.findByPersonCivilRegistrationNumberAndInstitution(request.cpr(), institution);
		if (people.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active employee with the given civil registration number in the institution with institution number " + request.institutionNumber());
		}
		for (DBInstitutionPerson person : people) {
			if (person.isDeleted()) continue;

			person.setDeleted(true);
			person.setStilDeleted(LocalDateTime.now());
			person.setLastModified(LocalDateTime.now());
		}
	}

	private boolean isNullOrBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private boolean containsNonExternalRoles(EmployeeImportDTO importDTO) {
		Set<PossibleImportSubrole> allowed = Set.of(PossibleImportSubrole.PRAKTIKANT, PossibleImportSubrole.EKSTERN);
		return importDTO.getRoles().stream().anyMatch(role -> !allowed.contains(role));
	}

	private boolean containsNonEmployeeRoles(EmployeeImportDTO importDTO) {
		Set<PossibleImportSubrole> allowed = Set.of(
				PossibleImportSubrole.LÆRER,
				PossibleImportSubrole.PÆDAGOG,
				PossibleImportSubrole.VIKAR,
				PossibleImportSubrole.LEDER,
				PossibleImportSubrole.LEDELSE,
				PossibleImportSubrole.TAP,
				PossibleImportSubrole.KONSULENT
		);
		return importDTO.getRoles().stream().anyMatch(role -> !allowed.contains(role));
	}
}