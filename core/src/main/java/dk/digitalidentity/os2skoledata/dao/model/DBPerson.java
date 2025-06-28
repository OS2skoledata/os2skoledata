package dk.digitalidentity.os2skoledata.dao.model;

import java.time.LocalDate;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBGender;
import dk.digitalidentity.os2skoledata.util.DateUtils;
import https.wsieksport_unilogin_dk.eksport.fullmyndighed.PersonFullMyndighed;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "person")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBPerson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String aliasFamilyName;
	
	@Column
	private String aliasFirstName;

	@Column
	private LocalDate birthDate;
	
	@Column
	private String civilRegistrationNumber;
	
	@Column
	private String emailAddress;
	
	@Column
	private String familyName;
	
	@Column
	private String firstName;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBGender gender;
	
	@Column
	private String photoId;
	
	@Column
	private int verificationLevel;
	
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "protected")
	private boolean _protected;

	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "address_id", nullable = true)
	private DBAddress address;
	
	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "home_phone_number_id", nullable = true)
	private DBPhoneNumber homePhoneNumber;
	
	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "mobile_phone_number_id", nullable = true)
	private DBPhoneNumber mobilePhoneNumber;
	
	@BatchSize(size = 100)
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "work_phone_number_id", nullable = true)
	private DBPhoneNumber workPhoneNumber;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "person", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Affiliation> affiliations;

	public boolean apiEquals(PersonFullMyndighed person, boolean requiredOnly) {
		if (person == null) {
			return false;
		}
		
		if (!Objects.equals(this.aliasFamilyName, person.getAliasFamilyName())) {
			log.debug("DBPerson: Not equals on 'aliasFamilyName' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.aliasFirstName, person.getAliasFirstName())) {
			log.debug("DBPerson: Not equals on 'aliasFirstName' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.civilRegistrationNumber, person.getCivilRegistrationNumber())) {
			log.debug("DBPerson: Not equals on 'civilRegistrationNumber' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.emailAddress, person.getEmailAddress())) {
			log.debug("DBPerson: Not equals on 'emailAddress' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.familyName, person.getFamilyName())) {
			log.debug("DBPerson: Not equals on 'familyName' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.firstName, person.getFirstName())) {
			log.debug("DBPerson: Not equals on 'firstName' for " + this.id);
			return false;
		}
		
		if (!Objects.equals(this.birthDate, DateUtils.fromXMLGregorianCalendar(person.getBirthDate()))) {
			log.debug("DBPerson: Not equals on 'birthDate' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.verificationLevel, person.getVerificationLevel())) {
			log.debug("DBPerson: Not equals on 'verificationLevel' for " + this.id);
			return false;
		}

		if (!Objects.equals(this._protected, person.isProtected())) {
			log.debug("DBPerson: Not equals on 'protected' for " + this.id);
			return false;
		}

		if (!requiredOnly) {
			if (!Objects.equals(this.photoId, person.getPhotoId())) {
				log.debug("DBPerson: Not equals on 'photoId' for " + this.id);
				return false;
			}

			if ((this.gender == null && person.getGender() != null) ||
					(this.gender != null && person.getGender() == null) ||
					(this.gender != null && person.getGender() != null && !Objects.equals(this.gender.name(), person.getGender().name()))) {

				log.debug("DBPerson: Not equals on 'gender' for " + this.id);
				return false;
			}

			if ((this.address == null && person.getAddress() != null) ||
					(this.address != null && !this.address.apiEquals(person.getAddress()))) {

				log.debug("DBPerson: Not equals on 'address' for " + this.id);
				return false;
			}

			if ((this.homePhoneNumber == null && person.getHomePhoneNumber()!= null) ||
					(this.homePhoneNumber != null && !this.homePhoneNumber.apiEquals(person.getHomePhoneNumber()))) {

				log.debug("DBPerson: Not equals on 'homePhoneNumber' for " + this.id);
				return false;
			}

			if ((this.mobilePhoneNumber == null && person.getMobilePhoneNumber() != null) ||
					(this.mobilePhoneNumber != null && !this.mobilePhoneNumber.apiEquals(person.getMobilePhoneNumber()))) {

				log.debug("DBPerson: Not equals on 'mobilePhoneNumber' for " + this.id);
				return false;
			}

			if ((this.workPhoneNumber == null && person.getWorkPhoneNumber() != null) ||
					(this.workPhoneNumber != null && !this.workPhoneNumber.apiEquals(person.getWorkPhoneNumber()))) {

				log.debug("DBPerson: Not equals on 'workPhoneNumber' for " + this.id);
				return false;
			}
		}

		return true;
	}

	public void copyFields(PersonFullMyndighed person, boolean requiredOnly) {
		if (person == null) {
			return;
		}
		
		this.aliasFamilyName = person.getAliasFamilyName();
		this.aliasFirstName = person.getAliasFirstName();
		this.birthDate = DateUtils.fromXMLGregorianCalendar(person.getBirthDate());
		this.civilRegistrationNumber = person.getCivilRegistrationNumber();
		this.emailAddress = person.getEmailAddress();
		this.familyName = person.getFamilyName();
		this.firstName = person.getFirstName();
		this._protected = person.isProtected();
		this.verificationLevel = person.getVerificationLevel();

		if (!requiredOnly) {
			if (person.getGender() != null) {
				this.gender = DBGender.from(person.getGender());
			}
			else {
				this.gender = null;
			}

			this.photoId = person.getPhotoId();

			if (this.address == null && person.getAddress() != null) {
				this.address = new DBAddress();
			}

			if (this.address != null) {
				this.address.copyFields(person.getAddress());
			}

			if (this.homePhoneNumber == null && person.getHomePhoneNumber() != null) {
				this.homePhoneNumber = new DBPhoneNumber();
			}

			if (this.homePhoneNumber != null) {
				this.homePhoneNumber.copyFields(person.getHomePhoneNumber());
			}

			if (this.mobilePhoneNumber == null && person.getMobilePhoneNumber() != null) {
				this.mobilePhoneNumber = new DBPhoneNumber();
			}

			if (this.mobilePhoneNumber != null) {
				this.mobilePhoneNumber.copyFields(person.getMobilePhoneNumber());
			}

			if (this.workPhoneNumber == null && person.getWorkPhoneNumber() != null) {
				this.workPhoneNumber = new DBPhoneNumber();
			}

			if (this.workPhoneNumber != null) {
				this.workPhoneNumber.copyFields(person.getWorkPhoneNumber());
			}
		}
	}

	public boolean isProtected() {
		return _protected;
	}

	public void setProtected(boolean value) {
		this._protected = value;
	}

	public static String getName(DBPerson person) {
		if (person == null) {
			return "";
		}

		if (person.isProtected()) {
			return person.getAliasFirstName() + " " + person.getAliasFamilyName();
		} else {
			return person.getFirstName() + " " + person.getFamilyName();
		}
	}
}
