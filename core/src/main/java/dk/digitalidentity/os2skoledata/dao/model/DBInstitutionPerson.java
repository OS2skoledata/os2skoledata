package dk.digitalidentity.os2skoledata.dao.model;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import https.wsieksport_unilogin_dk.eksport.fullmyndighed.InstitutionPersonFullMyndighed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "institutionperson")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBInstitutionPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@LastModifiedDate
	private LocalDateTime lastModified;

	@Column
	private boolean deleted;

	@Column
	private String source;
	
	@Column
	private String localPersonId;

	// from the Active Directory integration
	@Column
	private String username;

	//Person can be one of the 3 types Employee, Extern, Student
	
	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "employee_id", nullable = true)
	private DBEmployee employee;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "extern_id", nullable = true)
	private DBExtern extern;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "student_id", nullable = true)
	private DBStudent student;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "person_id", nullable = true)
	private DBPerson person;
	
	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "unilogin_id", nullable = false)
	private DBUniLogin uniLogin;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_id")
	private DBInstitution institution;

	// create / delete dates
	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime stilCreated;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime stilDeleted;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime adCreated;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime adDeactivated;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime gwCreated;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime gwDeactivated;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime azureCreated;

	@Column
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private LocalDateTime azureDeactivated;

	public boolean apiEquals(InstitutionPersonFullMyndighed other) {
		if (other == null) {
			return false;
		}
		
		if (!Objects.equals(this.source, other.getSource())) {
			log.debug("DBInstitutionPerson: Not equals on 'source' for " + this.id);
			return false;
		}
		
		if (!Objects.equals(this.localPersonId, other.getLocalPersonId())) {
			log.debug("DBInstitutionPerson: Not equals on 'localPersonId' for " + this.id);
			return false;
		}
		
		if ((this.employee == null && other.getEmployee() != null) || 
			(this.employee != null && !this.employee.apiEquals(other.getEmployee()))) {
			
			log.debug("DBInstitutionPerson: Not equals on 'employee' for " + this.id);
			return false;
		}

		if ((this.extern == null && other.getExtern() != null) ||
			(this.extern != null && !this.extern.apiEquals(other.getExtern()))) {
			
			log.debug("DBInstitutionPerson: Not equals on 'extern' for " + this.id);
			return false;
		}

		if ((this.person == null && other.getPerson() != null) ||
			(this.person != null && !this.person.apiEquals(other.getPerson()))) {
			
			log.debug("DBInstitutionPerson: Not equals on 'person' for " + this.id);
			return false;
		}

		if ((this.student == null && other.getStudent() != null) ||
			(this.student != null && !this.student.apiEquals(other.getStudent()))) {
			
			log.debug("DBInstitutionPerson: Not equals on 'student' for " + this.id);
			return false;
		}

		if ((this.uniLogin == null && other.getUNILogin() != null) ||
			(this.uniLogin != null && !this.uniLogin.apiEquals(other.getUNILogin()))) {
			
			log.debug("DBInstitutionPerson: Not equals on 'uniLogin' for " + this.id);
			return false;
		}
		
		return true;
	}

	public void copyFields(InstitutionPersonFullMyndighed institutionPerson) {
		if (institutionPerson == null) {
			return;
		}

		this.localPersonId = institutionPerson.getLocalPersonId();
		this.source = institutionPerson.getSource();

		if (this.employee == null && institutionPerson.getEmployee() != null) {
			this.employee = new DBEmployee();
		}

		if (this.employee != null) {
			this.employee.copyFields(institutionPerson.getEmployee());
		}

		if (this.extern == null && institutionPerson.getExtern() != null) {
			this.extern = new DBExtern();
		}
		
		if (this.extern != null) {
			this.extern.copyFields(institutionPerson.getExtern());
		}

		if (this.person == null && institutionPerson.getPerson() != null) {
			this.person = new DBPerson();
		}
		
		if (this.person != null) {
			this.person.copyFields(institutionPerson.getPerson());
		}

		if (this.student == null && institutionPerson.getStudent() != null) {
			this.student = new DBStudent();
		}
		
		if (this.student != null) {
			this.student.copyFields(institutionPerson.getStudent());
		}

		if (this.uniLogin == null && institutionPerson.getUNILogin() != null) {
			this.uniLogin = new DBUniLogin();
		}
		
		if (this.uniLogin != null) {
			this.uniLogin.copyFields(institutionPerson.getUNILogin());
		}
	}
}
