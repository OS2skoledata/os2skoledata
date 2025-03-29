package dk.digitalidentity.os2skoledata.controller.rest;

import java.util.UUID;

import javax.validation.Valid;

import dk.digitalidentity.os2skoledata.dao.model.enums.ClientAccessRole;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import dk.digitalidentity.os2skoledata.dao.model.Client;
import dk.digitalidentity.os2skoledata.service.ClientService;

@RequireAdministratorRole
@RestController
public class ClientRestController {

	@Autowired
	private ClientService clientService;

	record ClientRecord(long id, String name, String apiKey, boolean paused, ClientAccessRole accessRole) {}

	@PostMapping("/rest/clients/create")
	public ResponseEntity<?> createClient(@Valid @RequestBody ClientRecord body) {
		Client client = clientService.findByName(body.name);
		
		if (client != null) {
			return ResponseEntity.badRequest().body("Der findes allerede klient med dette navn.");
		}

		client = new Client();
		client.setName(body.name);
		client.setApiKey(UUID.randomUUID().toString());
		client.setAccessRole(body.accessRole);

		clientService.save(client);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/rest/clients/delete")
	public ResponseEntity<?> deleteClient(@Valid @RequestBody ClientRecord body) {
		Client client = clientService.findById(body.id);
		
		if (client != null) {
			clientService.delete(client);
		}
		else {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok().build();
	}

	@PostMapping("/rest/clients/pause")
	public ResponseEntity<?> pauseClient(@Valid @RequestBody ClientRecord body) {
		Client client = clientService.findById(body.id);
		
		if (client != null) {
			client.setPaused(body.paused);
			clientService.save(client);
		}
		else {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok().build();
	}
}
