package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBPasswordState;
import https.unilogin_dk.data.transitional.UniLoginFull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Audited
@Entity
@Table(name = "unilogin")
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class DBUniLogin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String civilRegistrationNumber;
	
	@Column
	private String initialPassword;

	@Column
	private String name;
	
	@Column
	@Enumerated(EnumType.STRING)
	private DBPasswordState passwordState;
	
	@Column
	private String userId;

	public boolean apiEquals(UniLoginFull other) {
		if (other == null) {
			return false;
		}

		if (!Objects.equals(this.civilRegistrationNumber, other.getCivilRegistrationNumber())) {
			log.debug("DBUniLogin: Not equals on 'civilRegistrationNumber' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.initialPassword, other.getInitialPassword())) {
			log.debug("DBUniLogin: Not equals on 'initialPassword' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.name, other.getName())) {
			log.debug("DBUniLogin: Not equals on 'name' for " + this.id);
			return false;
		}

		if ((this.passwordState == null && other.getPasswordState() != null) ||
			(this.passwordState != null && other.getPasswordState() == null) ||
			(!Objects.equals(this.passwordState.name(), other.getPasswordState().name()))) {

			log.debug("DBUniLogin: Not equals on 'passwordState' for " + this.id);
			return false;
		}

		if (!Objects.equals(this.userId, other.getUserId())) {
			log.debug("DBUniLogin: Not equals on 'userId' for " + this.id);
			return false;
		}

		return true;
	}

	public void copyFields(UniLoginFull uniLogin) {
		if (uniLogin == null) {
			return;
		}
		
		this.civilRegistrationNumber = uniLogin.getCivilRegistrationNumber();
		this.initialPassword = uniLogin.getInitialPassword();
		this.name = uniLogin.getName();
		
		if (uniLogin.getPasswordState() != null) {
			this.passwordState = DBPasswordState.from(uniLogin.getPasswordState());
		}
		else {
			this.passwordState = null;
		}
		
		this.userId = uniLogin.getUserId();
	}
}
