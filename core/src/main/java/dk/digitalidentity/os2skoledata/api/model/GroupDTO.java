package dk.digitalidentity.os2skoledata.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dk.digitalidentity.os2skoledata.dao.model.enums.DBImportGroupType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class GroupDTO {
	private long databaseId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime lastModified;

	private LocalDate fromDate;
	private LocalDate toDate;
	private String groupId;
	private String groupLevel;
	private String groupName;
	private DBImportGroupType groupType;
	private String line;
	private String institutionNumber;
	private String institutionName;
	private boolean institutionLocked;
	private String institutionGoogleWorkspaceId;
	private String studentInstitutionGoogleWorkspaceId;
	private String employeeInstitutionGoogleWorkspaceId;
	private String googleWorkspaceId;
	private String driveGoogleWorkspaceId;
	private String azureSecurityGroupId;
	private int startYear;
	private String groupGoogleWorkspaceEmail;
	private String groupOnlyStudentsGoogleWorkspaceEmail;
	private String azureTeamId;
}
