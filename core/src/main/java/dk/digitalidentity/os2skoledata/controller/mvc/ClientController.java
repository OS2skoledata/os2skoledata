package dk.digitalidentity.os2skoledata.controller.mvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.ClientService;

@Controller
@RequireAdministratorRole
public class ClientController {
	
	@Autowired
	private ClientService clientService;

	@GetMapping("/ui/clients")
	public String list(Model model) {
		List<Client> clients = clientService.findAll();

		model.addAttribute("clients", clients);

		return "client/list";
	}

	record ClientRecord(long id, String name, String apiKey, LocalDateTime lastActive, boolean paused) {}

	@RequestMapping(value = "/ui/clients/clientTable")
	public String clientListFragment(Model model) {
		
		List<ClientRecord> clients = clientService.findAll().stream().map(c -> new ClientRecord(c.getId(), c.getName(), c.getApiKey(), c.getLastActive(), c.isPaused())).collect(Collectors.toList());
		model.addAttribute("clients", clients);

		return "client/fragments/clientTable :: clientTable";
	}
}
