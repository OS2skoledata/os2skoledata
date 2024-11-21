package dk.digitalidentity.os2skoledata.dao.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.LogAction;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditlogs")
@Setter
@Getter
public class AuditLog {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@CreationTimestamp
	@Column
	private LocalDateTime tts;
		
	@Column
	private String ipAddress;

	// referenced person which data was used

	@Column
	private String personName;

	@Column
	private String personUsername;

	// referenced entity that performed the action (null if the performer was the data-owner)

	@Column
	private String performerUsername;
	
	@Column
	private String performerName;
	
	// action performed

	@Column
	@Enumerated(EnumType.STRING)
	private LogAction logAction;

	@Column
	private String details;
}
