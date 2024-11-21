package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class PasswordSetting {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GradeGroup gradeGroup;

	@Column
	private long minLength;

	@Column(name = "complex_password")
	private boolean requireComplexPassword;
}