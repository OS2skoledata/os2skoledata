package dk.digitalidentity.os2skoledata.service.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LatestModificationsWrapper {
	private Long latestRevisionNumber;
	private List<AuditWrapper> institutionChanges;
	private List<AuditWrapper> institutionPersonChanges;
	private List<AuditWrapper> groupChanges;
}
