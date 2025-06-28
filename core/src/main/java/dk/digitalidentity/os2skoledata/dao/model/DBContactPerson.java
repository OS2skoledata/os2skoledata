package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBRelationType;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.ContactPersonFullMyndighed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "contact_person")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBContactPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private Integer accessLevel;
	
	@Column
	private String cvr;

	@Column
	private String pnr;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBRelationType relation;
	
	@Column
	private boolean childCustody;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "unilogin_id", nullable = true)
	private DBUniLogin uniLogin;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "person_id", nullable = false)
	private DBPerson person;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private DBStudent student;

	public boolean apiEquals(ContactPersonFullMyndighed contactPerson) {
		if (contactPerson == null) {
			return false;
		}

		if ((this.accessLevel == null && contactPerson.getAccessLevel() != null) ||
			(this.accessLevel != null && contactPerson.getAccessLevel() == null) ||
			((this.accessLevel != null && contactPerson.getAccessLevel() != null && (this.accessLevel.intValue() != contactPerson.getAccessLevel().intValue())))) {
			
			log.debug("DBContactPerson: Not equals on 'accessLevel' for " + this.id + " local: " + this.accessLevel + " stil: " + contactPerson.getAccessLevel());
			return false;
		}

		if (!Objects.equals(this.cvr, contactPerson.getCvr())) {
			log.debug("DBContactPerson: Not equals on 'cvr' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.pnr, contactPerson.getPnr())) {
			log.debug("DBContactPerson: Not equals on 'pnr' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.childCustody, contactPerson.isChildCustody())) {
			log.debug("DBContactPerson: Not equals on 'childCustody' for " + this.id + " local: " + this.childCustody + " stil: " + contactPerson.isChildCustody());
			return false;
		}

		if ((this.relation == null && contactPerson.getRelation() != null) ||
			(this.relation != null && contactPerson.getRelation() == null) ||
			(this.relation != null && contactPerson.getRelation() != null && !Objects.equals(this.relation.name(), contactPerson.getRelation().name()))) {
			
			log.debug("DBContactPerson: Not equals on 'relation' for " + this.id + " local: " + this.relation.name() + " stil: " + contactPerson.getRelation().name());
			return false;
		}

		if ((this.person == null && contactPerson.getPerson() != null) ||
			(this.person != null && !this.person.apiEquals(contactPerson.getPerson(), false))) {

			log.debug("DBContactPerson: Not equals on 'person' for " + this.id);
			return false;
		}

		if ((this.uniLogin == null && contactPerson.getUNILogin() != null) ||
			(this.uniLogin != null && !this.uniLogin.apiEquals(contactPerson.getUNILogin()))) {

			log.debug("DBContactPerson: Not equals on 'uniLogin' for " + this.id);
			return false;
		}

		return true;
	}

	public void copyFields(ContactPersonFullMyndighed contactPerson) {
		if (contactPerson == null) {
			return;
		}
		
		this.accessLevel = contactPerson.getAccessLevel();
		this.cvr = contactPerson.getCvr();
		this.childCustody = contactPerson.isChildCustody();
		this.pnr = contactPerson.getPnr();
		
		if (contactPerson.getRelation() != null) {
			this.relation = DBRelationType.from(contactPerson.getRelation());
		}
		else {
			this.relation = null;
		}
		
		if (this.person == null && contactPerson.getPerson() != null) {
			this.person = new DBPerson();
		}
		
		if (this.person != null) {
			this.person.copyFields(contactPerson.getPerson(), false);
		}

		if (this.uniLogin == null && contactPerson.getUNILogin() != null) {
			this.uniLogin = new DBUniLogin();
		}
		
		if (this.uniLogin != null) {
			this.uniLogin.copyFields(contactPerson.getUNILogin());
		}
	}
}
