package dk.digitalidentity.os2skoledata.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dk.digitalidentity.os2skoledata.api.model.enums.PersonRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBExternalRoleType;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBGender;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBStudentRole;
import dk.digitalidentity.os2skoledata.service.model.ContactCardDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class InstitutionPersonDTO {
	private long databaseId;
	private String localPersonId;
	private String source;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastModified;

	private String firstName;
	private String familyName;
	private DBGender gender;
	private String cpr;
	private String username;
	private String stilUsername;
	private String uniId;
	private PersonRole role;
	private PersonRole globalRole;
	private List<Long> groupIds;
	private List<Long> studentMainGroups;
	private List<MiniGroupDTO> studentMainGroupsAsObjects;
	private List<String> studentMainGroupsGoogleWorkspaceIds;
	private String stilMainGroupCurrentInstitution;
	private List<String> stilGroupsCurrentInstitution;
	private List<InstitutionDTO> institutions;
	private String currentInstitutionNumber;
	private DBStudentRole studentRole;
	private DBStudentRole globalStudentRole;
	private List<DBEmployeeRole> employeeRoles;
	private Set<DBEmployeeRole> globalEmployeeRoles;
	private DBExternalRoleType externalRole;
	private DBExternalRoleType globalExternalRole;
	private InstitutionDTO currentInstitution;
	private int studentMainGroupStartYearForInstitution;
	private String studentMainGroupLevelForInstitution;
	private boolean deleted;
	private List<String> totalRoles;
	private List<ContactCardDTO> contactCards;
	private boolean setPasswordOnCreate;
	private String password;
}