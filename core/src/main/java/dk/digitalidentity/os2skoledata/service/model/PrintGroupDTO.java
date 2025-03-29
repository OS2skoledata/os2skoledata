package dk.digitalidentity.os2skoledata.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PrintGroupDTO {
	private long id;
	private String groupName;
	private String level;
	private String institutionName;
	private boolean canPrintPassword;
}
