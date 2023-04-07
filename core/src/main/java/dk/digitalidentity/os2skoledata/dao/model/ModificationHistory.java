package dk.digitalidentity.os2skoledata.dao.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

	// only for persons. List of groups person is member of.
	@Column
	@Convert(converter = StringListConverter.class)
	private List<String> groups;
}
