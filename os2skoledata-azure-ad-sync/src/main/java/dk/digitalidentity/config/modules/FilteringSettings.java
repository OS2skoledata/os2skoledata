package dk.digitalidentity.config.modules;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FilteringSettings {
	private List<String> globallyExcludedRoles = new ArrayList<>();
	private Map<String, List<String>> exludedRolesInInstitution = new HashMap<>();
}
