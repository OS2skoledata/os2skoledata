package dk.digitalidentity.os2skoledata.config.modules;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SyncSettings {
	private boolean onlySaveNeededPropertiesFromSTIL = false;
	private SyncFrom syncFrom = SyncFrom.STIL;

	// only if API_AND_STIL
	private List<SyncField> fieldsMaintainedBySTIL = new ArrayList<>();

	// used as placeholder if no uniID given
	private String uniIDPlaceholder = "N/A";

	/*
	* if syncFrom is API_AND_STIL we don't normally allow STIL to create and delete users,
	* but it can be needed for the initial load or during a test period when the municipality implements against the import API.
	* if transitionMode is true we will allow STIL to create all users and delete users where source != localSource. No updates.
	*/
	private boolean transitionMode = false;
	private String localSource = "local";

}
