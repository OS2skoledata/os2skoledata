package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.BatchSize;
import org.hibernate.envers.Audited;

import dk.digitalidentity.os2skoledata.dao.model.enums.DBPasswordState;
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
@BatchSize(size = 100)
public class DBUniLogin {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	// no longer in STIL payload for WS17 v7
	@Column
	private String civilRegistrationNumber;

	// no longer in STIL payload for WS17 v7
	@Column
	private String initialPassword;

	// no longer in STIL payload for WS17 v7
	@Column
	private String name;

	// no longer in STIL payload for WS17 v7
	@Column
	@NotNull
	@Enumerated(EnumType.STRING)
	private DBPasswordState passwordState = DBPasswordState.UNKNOWN;

	// still in use
	@Column
	private String userId;

	public boolean apiEquals(String userId) {
		if (userId == null) {
			return false;
		}

		if (!Objects.equals(this.userId, userId)) {
			log.debug("DBUniLogin: Not equals on 'userId' for " + this.id);
			return false;
		}

		return true;
	}

	public void copyFields(String userId) {
		if (userId == null) {
			return;
		}

		this.passwordState = DBPasswordState.UNKNOWN;
		this.userId = userId;
	}
}
