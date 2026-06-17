package dk.digitalidentity.os2skoledata.controller.mvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dk.digitalidentity.os2skoledata.dao.model.enums.ClientAccessRole;
import dk.digitalidentity.os2skoledata.dao.model.enums.EntityType;
import dk.digitalidentity.os2skoledata.service.model.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.ClientService;
import dk.digitalidentity.os2skoledata.service.GroupService;

@Controller
@RequireAdministratorRole
public class QueueController {
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private ClientService clientService;

	record GroupRecord( String groupId, String groupName) {}
	record ClientRecord(long id, String name) {}
	
	@GetMapping(path = { "/ui/showQueue" })
	public String queue(Model model) {

		List<GroupRecord> groups = groupService.findAll().stream().map(g -> new GroupRecord(g.getGroupId(), g.getGroupName())).collect(Collectors.toList());
		model.addAttribute("groups", groups);

		List<ClientRecord> clients = clientService.findAll().stream()
				.filter(c -> !c.getAccessRole().equals(ClientAccessRole.IMPORT_ACCESS))
				.map(c -> new ClientRecord(c.getId(), c.getName())).collect(Collectors.toList());
		model.addAttribute("clients", clients);

		Map<String, String> entityTypeLabels = new HashMap<>();
		for (EntityType type : EntityType.values()) {
			entityTypeLabels.put(type.name(), type.getMessage());
		}
		model.addAttribute("entityTypeLabels", entityTypeLabels);

		Map<String, String> eventTypeLabels = new HashMap<>();
		for (EventType type : EventType.values()) {
			eventTypeLabels.put(type.name(), type.getMessage());
		}
		model.addAttribute("eventTypeLabels", eventTypeLabels);

		return "queue/list";
	}
}
