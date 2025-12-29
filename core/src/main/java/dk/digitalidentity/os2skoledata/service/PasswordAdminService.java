package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.PasswordAdminDao;
import dk.digitalidentity.os2skoledata.dao.model.PasswordAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PasswordAdminService {

	@Autowired
	private PasswordAdminDao passwordAdminDao;

	public List<PasswordAdmin> getAll() {
		return passwordAdminDao.findAll();
	}

	public PasswordAdmin getByUsername(String username) {
		return passwordAdminDao.findByUsername(username);
	}

	public void delete(long id) {
		passwordAdminDao.deleteById(id);
	}

	public PasswordAdmin save(PasswordAdmin passwordAdmin) {
		return passwordAdminDao.save(passwordAdmin);
	}
}
