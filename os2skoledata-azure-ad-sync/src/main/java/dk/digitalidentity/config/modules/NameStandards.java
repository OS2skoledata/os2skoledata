package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameStandards {
	private String allInInstitutionSecurityGroupNameStandard;
	private String allStudentsInInstitutionSecurityGroupNameStandard;
	private String allEmployeesInInstitutionSecurityGroupNameStandard;
	private String classSecurityGroupNameStandard;
	private String classSecurityGroupNameStandardNoClassYear;
	private String globalEmployeeSecurityGroupName;
	private String globalStudentSecurityGroupName;
	private String allEmployeesInInstitutionTeamNameStandard;
	private String allEmployeesInInstitutionTeamMailStandard;
	private String classTeamNameStandard;
	private String classTeamNameStandardNoClassYear;
	private String classTeamMailStandard;
	private String classTeamMailStandardNoClassYear;
	private String levelSecurityGroupNameStandard;
	private String globalLevelSecurityGroupNameStandard;
}
