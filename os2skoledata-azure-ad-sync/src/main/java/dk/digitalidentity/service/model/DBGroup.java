package dk.digitalidentity.service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBGroup {
	private long databaseId;
	private String groupName;
	private String groupId;
	private String groupLevel;
	private String institutionNumber;
	private String institutionName;
	private String azureSecurityGroupId;
	private boolean institutionLocked;
	private int startYear;
	private String azureTeamId;
	private String line;
}
