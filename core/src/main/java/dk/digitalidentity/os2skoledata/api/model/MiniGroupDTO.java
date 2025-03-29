package dk.digitalidentity.os2skoledata.api.model;

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
}
