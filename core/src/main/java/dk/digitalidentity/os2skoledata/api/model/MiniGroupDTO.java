package dk.digitalidentity.os2skoledata.api.model;

import dk.digitalidentity.os2skoledata.dao.model.enums.InstitutionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MiniGroupDTO {
	private long databaseId;
	private int startYear;
	private String institutionName;
	private InstitutionType institutionType;
	private String workspaceId;
	private boolean primary;
}
