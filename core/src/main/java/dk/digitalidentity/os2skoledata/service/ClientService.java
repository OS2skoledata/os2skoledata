package dk.digitalidentity.os2skoledata.service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.ClientDao;
import dk.digitalidentity.os2skoledata.dao.model.Client;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ClientService {

	@Autowired
	private ClientDao clientDao;

	public List<Client> findAll() {
		return clientDao.findAll();
	}

	public Client save(Client client) {
		return clientDao.save(client);
	}

	public Client findById(long id) {
		return clientDao.findById(id);
	}

	public Client findByName(String name) {
		return clientDao.findByName(name);
	}
	
	public Client getClientByApiKey(String apiKey) {
		return clientDao.findByApiKey(apiKey);
	}

	public void delete(Client client) {
		clientDao.delete(client);
	}

	@Transactional
	public void monitor() {
		LocalDateTime aWeekAgo = LocalDateTime.now().minusDays(7);
		for (Client client : findAll()) {
			if (client.isMonitor() && client.getLastFullSync() != null) {
				if (client.getLastFullSync().isBefore(aWeekAgo)) {
					log.error("Last full sync for client with name " + client.getName() + " and id " + client.getId() + " has not been seen for a week");
				}
			}
		}
	}
}
