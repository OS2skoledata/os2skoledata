package dk.digitalidentity.service.model;

import dk.digitalidentity.service.model.enums.EntityType;
import dk.digitalidentity.service.model.enums.EventType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModificationHistory {
	private Long id;
	private long entityId;
	private EntityType entityType;
	private EventType eventType;
	private String entityName;
	private long institutionId;
	private String institutionName;

	// only for EntityType person
	private List<String> groups;

}
