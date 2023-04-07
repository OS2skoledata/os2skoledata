package dk.digitalidentity.os2skoledata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.digitalidentity.os2skoledata.dao.ClientDao;
import dk.digitalidentity.os2skoledata.dao.model.Client;

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
}
