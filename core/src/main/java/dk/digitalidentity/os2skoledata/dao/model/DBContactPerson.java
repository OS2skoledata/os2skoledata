package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Objects;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.fullmyndighed.ContactPersonFullMyndighed;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBRelationType;
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

	// no longer in STIL payload for WS17 v7
	@Column
	private String cvr;

	// no longer in STIL payload for WS17 v7
	@Column
	private String pnr;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBRelationType relation;
	
	@Column
	private boolean childCustody;

	// no longer in STIL payload for WS17 v7
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "unilogin_id", nullable = true)
	private DBUniLogin uniLogin;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "person_id", nullable = false)
	private DBPerson person;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private DBStudent student;

	public boolean apiEquals(ContactPersonFullMyndighed contactPerson) {
		if (contactPerson == null) {
			return false;
		}

		if (!Objects.equals(this.accessLevel, contactPerson.getAccessLevel())) {
			log.debug("DBContactPerson: Not equals on 'accessLevel' for " + this.id + " local: " + this.accessLevel + " stil: " + contactPerson.getAccessLevel());
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

		return true;
	}

	public void copyFields(ContactPersonFullMyndighed contactPerson) {
		if (contactPerson == null) {
			return;
		}
		
		this.accessLevel = contactPerson.getAccessLevel();
		this.childCustody = contactPerson.isChildCustody();

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
	}
}
