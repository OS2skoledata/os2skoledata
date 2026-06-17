package dk.digitalidentity.os2skoledata.dao.model;

import dk.stil.brugerdatabasen.common.v3.Ansatrolle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBEmployeeRole;
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
