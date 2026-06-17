package dk.digitalidentity.os2skoledata.datatables.dao.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.service.model.EventType;
import dk.digitalidentity.os2skoledata.util.StringListConverter;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "view_datatables_modification_history")
public class GridModificationHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String tts;

	@Column
	@Enumerated(EnumType.STRING)
	private EntityType entityType;

	@Column
	@Enumerated(EnumType.STRING)
	private EventType eventType;

	@Column
	private String entityName;

	@Column
	private String institutionName;

	@Column
	private long institutionId;

	@Column
	private long entityId;

	@Column
	private Long rev;

	@Column
	private String uniId;

	@Column
	private String username;

	@Column
	private String entityRole;

	@Column
	@Convert(converter = StringListConverter.class)
	private List<String> groups;
}
