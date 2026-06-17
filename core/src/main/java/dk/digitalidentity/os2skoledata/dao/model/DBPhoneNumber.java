package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Objects;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.common.PhoneNumberProtectable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "phonenumber")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@BatchSize(size = 100)
public class DBPhoneNumber {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String value;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@Column(name = "protected")
	private boolean _protected;

	public boolean apiEquals(PhoneNumberProtectable phoneNumber) {
		if (phoneNumber == null) {
			return false;
		}
		
		if (!Objects.equals(this.value, phoneNumber.getValue())) {
			log.debug("DBPhoneNumber: Not equals on 'value' for " + this.id);
			return false;
		}

		if (!Objects.equals(this._protected, phoneNumber.isProtected())) {
			log.debug("DBPhoneNumber: Not equals on 'protected' for " + this.id);
			return false;
		}
		
		return true;
	}

	public void copyFields(PhoneNumberProtectable phoneNumber) {
		if (phoneNumber == null) {
			return;
		}
		
		this.value = phoneNumber.getValue();
		this._protected = phoneNumber.isProtected();
	}

	public boolean isProtected() {
		return _protected;
	}

	public void setProtected(boolean value) {
		this._protected = value;
	}
}
