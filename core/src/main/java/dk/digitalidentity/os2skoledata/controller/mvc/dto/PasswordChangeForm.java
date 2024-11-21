package dk.digitalidentity.os2skoledata.controller.mvc.dto;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import dk.digitalidentity.os2skoledata.service.model.StudentPasswordChangeDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeForm implements Serializable {
	private static final long serialVersionUID = -286544720952319815L;

	private String username;
	private String personName;
	private String password;
	private String confirmPassword;

	public PasswordChangeForm(StudentPasswordChangeDTO student) {
		this.username = student.getUsername();
		this.personName = student.getName() + "(" + student.getUsername() + ")";
	}
}
