package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.service.model.EventType;
import dk.digitalidentity.os2skoledata.util.StringListConverter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ModificationHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(updatable = false)
	private Date tts;

	@Column
	private long entityId;

	@Column
	@Enumerated(EnumType.STRING)
	private EntityType entityType;

	@Column
	@Enumerated(EnumType.STRING)
	private EventType eventType;

	@Column
	private String entityName;

	@Column
	private long institutionId;

	@Column
	private String institutionName;

	@Column
	private long rev;

	@Column
	private String uniId;

	@Column
	private String username;

	@Column
	private String entityRole;

	// only for persons. List of groups person is member of.
	@Column
	@Convert(converter = StringListConverter.class)
	private List<String> groups;
}
