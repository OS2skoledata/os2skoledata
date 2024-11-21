package dk.digitalidentity.os2skoledata.datatables.dao.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.service.model.EventType;
import dk.digitalidentity.os2skoledata.util.StringListConverter;
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
	@Convert(converter = StringListConverter.class)
	private List<String> groups;
}
