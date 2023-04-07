package dk.digitalidentity.os2skoledata.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.digitalidentity.os2skoledata.dao.model.Client;

public interface ClientDao extends JpaRepository<Client, Long> {
	List<Client> findAll();
	Client findByApiKey(String apiKey);
	Client findByName(String name);
	Client findById(long id);
}