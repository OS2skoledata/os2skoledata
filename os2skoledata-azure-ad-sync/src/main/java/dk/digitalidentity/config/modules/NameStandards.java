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
	private String globalEmployeeSecurityGroupName;
	private String globalStudentSecurityGroupName;
}
