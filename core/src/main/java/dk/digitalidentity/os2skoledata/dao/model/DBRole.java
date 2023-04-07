package dk.digitalidentity.os2skoledata.dao.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
import https.unilogin.Ansatrolle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Audited
@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class DBRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	@Enumerated(EnumType.STRING)
	private DBEmployeeRole employeeRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id")
	private DBEmployee employee;

	public DBRole(Ansatrolle ansatrolle) {
		this.employeeRole = DBEmployeeRole.from(ansatrolle);
	}
}
