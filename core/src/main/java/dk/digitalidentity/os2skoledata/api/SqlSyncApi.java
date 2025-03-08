package dk.digitalidentity.os2skoledata.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import dk.digitalidentity.os2skoledata.dao.model.DBAddress;
import dk.digitalidentity.os2skoledata.dao.model.DBContactPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBEmployeeGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBExternGroupId;
import dk.digitalidentity.os2skoledata.dao.model.DBGroup;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitution;
import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.dao.model.DBPhoneNumber;
import dk.digitalidentity.os2skoledata.dao.model.DBStudentGroupId;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBCountryCode;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBGender;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBPasswordState;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBRelationType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import dk.digitalidentity.os2skoledata.service.InstitutionService;

@RestController
public class SqlSyncApi {

	@Autowired
	private InstitutionService institutionService;

	record InstitutionFullRecord(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastModified, boolean deleted, String institutionName,
								 String institutionNumber, InstitutionType type, List<InstitutionPersonFullRecord> institutionPersons,
								 List<GroupFullRecord> groups) {	}

	record InstitutionPersonFullRecord(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastModified, boolean deleted,
			String source, String localPersonId, String username, EmployeeFullRecord employee, ExternFullRecord extern,
			StudentFullRecord student, PersonFullRecord person, UniLoginFullRecord uniLogin,
			@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime stilCreated, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime stilDeleted,
			@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime adCreated, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime adDeactivated,
			@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime gwCreated, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime gwDeactivated,
			@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime azureCreated, @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime azureDeactivated) { }

	record GroupFullRecord(@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime lastModified, boolean deleted,
			@JsonFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
			@JsonFormat(pattern = "yyyy-MM-dd") LocalDate toDate, String groupId, String groupLevel,
			String groupName, DBImportGroupType groupType, String line) { }
	
	record EmployeeFullRecord(String location, String occupation, String shortName, List<DBEmployeeRole> roles, List<String> groupIds) { }
	record ExternFullRecord(DBExternalRoleType role, List<String> groupIds) { }
	record ContactPersonFullRecord(Integer accessLevel, String cvr, String pnr, DBRelationType relation, boolean childCustody, UniLoginFullRecord uniLogin, PersonFullRecord person) { }
	record StudentFullRecord(String level, String location, String mainGroupId, DBStudentRole role, String studentNumber, List<ContactPersonFullRecord> contactPersons, List<String> groupIds) { }

	record AddressFullRecord(String country, DBCountryCode countryCode, String municipalityCode, String municipalityName, String postalCode, String postalDistrict, String streetAddress) {
		public static AddressFullRecord fromDBAddress(DBAddress address) {
			if (address == null) {
				return null;
			}

			return new AddressFullRecord(address.getCountry(), address.getCountryCode(),
					address.getMunicipalityCode(), address.getMunicipalityName(), address.getPostalCode(),
					address.getPostalDistrict(), address.getStreetAddress());
		}
	}

	record PhoneNumberFullRecord(String value, @JsonProperty("protected") boolean _protected) {

		public static PhoneNumberFullRecord fromDBPhonNumber(DBPhoneNumber phoneNumber) {
			if (phoneNumber == null) {
				return null;
			}

			return new PhoneNumberFullRecord(phoneNumber.getValue(), phoneNumber.isProtected());
		}
	}

	record PersonFullRecord(String aliasFamilyName, String aliasFirstName, @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
			String civilRegistrationNumber, String emailAddress, String familyName, String firstName, DBGender gender,
			String photoId, int verificationLevel, @JsonProperty("protected") boolean _protected, AddressFullRecord address,
			PhoneNumberFullRecord homePhoneNumber, PhoneNumberFullRecord mobilePhoneNumber,
			PhoneNumberFullRecord workPhoneNumber) { }

	record UniLoginFullRecord(String civilRegistrationNumber, String initialPassword, String name, DBPasswordState passwordState, String userId) { }

	@GetMapping("/api/full/{number}")
	public ResponseEntity<?> getInstitutionFull(@PathVariable("number") String institutionNumber) {
		DBInstitution institution = institutionService.findByInstitutionNumber(institutionNumber);
		
		List<InstitutionPersonFullRecord> institutionPersons = new ArrayList<>();
		for (DBInstitutionPerson institutionPerson : institution.getInstitutionPersons()) {
			
			EmployeeFullRecord employeeRecord = null;
			if (institutionPerson.getEmployee() != null) {
				List<DBEmployeeRole> employeeRoles = institutionPerson.getEmployee().getRoles().stream().map(r -> r.getEmployeeRole()).toList();
				employeeRecord = new EmployeeFullRecord(institutionPerson.getEmployee().getLocation(), institutionPerson.getEmployee().getOccupation(), institutionPerson.getEmployee().getShortName(), employeeRoles, institutionPerson.getEmployee().getGroupIds().stream().map(DBEmployeeGroupId::getGroupId).toList());
			}
			
			ExternFullRecord externRecord = null;
			if (institutionPerson.getExtern() != null) {
				externRecord = new ExternFullRecord(institutionPerson.getExtern().getRole(), institutionPerson.getExtern().getGroupIds().stream().map(DBExternGroupId::getGroupId).toList());
			}

			PersonFullRecord personRecord = new PersonFullRecord(
					institutionPerson.getPerson().getAliasFamilyName(),
					institutionPerson.getPerson().getAliasFirstName(),
					institutionPerson.getPerson().getBirthDate(),
					institutionPerson.getPerson().getCivilRegistrationNumber(),
					institutionPerson.getPerson().getEmailAddress(),
					institutionPerson.getPerson().getFamilyName(),
					institutionPerson.getPerson().getFirstName(),
					institutionPerson.getPerson().getGender(),
					institutionPerson.getPerson().getPhotoId(),
					institutionPerson.getPerson().getVerificationLevel(),
					institutionPerson.getPerson().isProtected(),
					AddressFullRecord.fromDBAddress(institutionPerson.getPerson().getAddress()),
					PhoneNumberFullRecord.fromDBPhonNumber(institutionPerson.getPerson().getHomePhoneNumber()),
					PhoneNumberFullRecord.fromDBPhonNumber(institutionPerson.getPerson().getMobilePhoneNumber()),
					PhoneNumberFullRecord.fromDBPhonNumber(institutionPerson.getPerson().getWorkPhoneNumber())
			);
					
			StudentFullRecord studentRecord = null;
			if (institutionPerson.getStudent() != null) {
				List<ContactPersonFullRecord> contactPersons = new ArrayList<>();
				for (DBContactPerson contactPerson : institutionPerson.getStudent().getContactPersons()) {

					UniLoginFullRecord contactPersonUniLogin = new UniLoginFullRecord(
							contactPerson.getUniLogin().getCivilRegistrationNumber(),
							contactPerson.getUniLogin().getInitialPassword(), contactPerson.getUniLogin().getName(),
							contactPerson.getUniLogin().getPasswordState(), contactPerson.getUniLogin().getUserId());

					PersonFullRecord pRecord = new PersonFullRecord(
							contactPerson.getPerson().getAliasFamilyName(),
							contactPerson.getPerson().getAliasFirstName(),
							contactPerson.getPerson().getBirthDate(),
							contactPerson.getPerson().getCivilRegistrationNumber(),
							contactPerson.getPerson().getEmailAddress(),
							contactPerson.getPerson().getFamilyName(),
							contactPerson.getPerson().getFirstName(),
							contactPerson.getPerson().getGender(),
							contactPerson.getPerson().getPhotoId(),
							contactPerson.getPerson().getVerificationLevel(),
							contactPerson.getPerson().isProtected(),
							AddressFullRecord.fromDBAddress(contactPerson.getPerson().getAddress()),
							PhoneNumberFullRecord.fromDBPhonNumber(contactPerson.getPerson().getHomePhoneNumber()),
							PhoneNumberFullRecord.fromDBPhonNumber(contactPerson.getPerson().getMobilePhoneNumber()),
							PhoneNumberFullRecord.fromDBPhonNumber(contactPerson.getPerson().getWorkPhoneNumber())
							);
					
					ContactPersonFullRecord contactPersonRecord = new ContactPersonFullRecord(contactPerson.getAccessLevel(), contactPerson.getCvr(),
							contactPerson.getPnr(), contactPerson.getRelation(), contactPerson.isChildCustody(), contactPersonUniLogin, pRecord);
					contactPersons.add(contactPersonRecord);
				}
				
				studentRecord = new StudentFullRecord(institutionPerson.getStudent().getLevel(),
						institutionPerson.getStudent().getLocation(), institutionPerson.getStudent().getMainGroupId(),
						institutionPerson.getStudent().getRole(), institutionPerson.getStudent().getStudentNumber(),
						contactPersons, institutionPerson.getStudent().getGroupIds().stream()
								.map(DBStudentGroupId::getGroupId).toList());
			}
			UniLoginFullRecord uniLoginRecord = new UniLoginFullRecord(
					institutionPerson.getUniLogin().getCivilRegistrationNumber(),
					institutionPerson.getUniLogin().getInitialPassword(), institutionPerson.getUniLogin().getName(),
					institutionPerson.getUniLogin().getPasswordState(), institutionPerson.getUniLogin().getUserId());
			InstitutionPersonFullRecord institutionPersonRecord = new InstitutionPersonFullRecord(
					institutionPerson.getLastModified(),
					institutionPerson.isDeleted(),
					institutionPerson.getSource(),
					institutionPerson.getLocalPersonId(),
					institutionPerson.getUsername(),
					employeeRecord,
					externRecord,
					studentRecord,
					personRecord,
					uniLoginRecord,
					institutionPerson.getStilCreated(),
					institutionPerson.getStilDeleted(),
					institutionPerson.getAdCreated(),
					institutionPerson.getAdDeactivated(),
					institutionPerson.getGwCreated(),
					institutionPerson.getGwDeactivated(),
					institutionPerson.getAzureCreated(),
					institutionPerson.getAzureDeactivated()
					);
			
			institutionPersons.add(institutionPersonRecord);
		}
		List<GroupFullRecord> groups = new ArrayList<>();
		for (DBGroup group : institution.getGroups()) {
			GroupFullRecord groupRecord = new GroupFullRecord(group.getLastModified(), group.isDeleted(),
					group.getFromDate(), group.getToDate(), group.getGroupId(), group.getGroupLevel(),
					group.getGroupName(), group.getGroupType(), group.getLine());
			groups.add(groupRecord);
		}

		InstitutionFullRecord result = new InstitutionFullRecord(institution.getLastModified(), institution.isDeleted(), institution.getInstitutionName(),
				institution.getInstitutionNumber(), institution.getType(), institutionPersons, groups);
		
		return ResponseEntity.ok(result);
	}
}
