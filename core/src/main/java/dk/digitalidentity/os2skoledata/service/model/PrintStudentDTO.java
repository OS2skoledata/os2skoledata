package dk.digitalidentity.os2skoledata.service.model;

import dk.digitalidentity.os2skoledata.dao.model.DBInstitutionPerson;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrintStudentDTO {
	private long id;
	private String name;
	private String username;
	private String uniId;
	private String password;

	public PrintStudentDTO(DBInstitutionPerson person, String password) {
		this.id = person.getId();
		this.name = person.getPerson().isProtected() ? person.getPerson().getAliasFirstName() + " " + person.getPerson().getAliasFamilyName() : person.getPerson().getFirstName() + " " + person.getPerson().getFamilyName();
		this.username = person.getUsername();
		this.uniId = person.getUniLogin().getUserId();
		this.password = password;
	}
}
