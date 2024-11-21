package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.dao.StudentPasswordChangeConfigurationDao;
import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentPasswordChangeConfigurationService {

    @Autowired
    private StudentPasswordChangeConfigurationDao studentPasswordChangeConfigurationDao;

    public StudentPasswordChangeConfiguration findById(long id) {
        return studentPasswordChangeConfigurationDao.findById(id);
    }

    public List<StudentPasswordChangeConfiguration> findAll() {
        return studentPasswordChangeConfigurationDao.findAll();
    }

    public void delete(long idToDelete) {
        studentPasswordChangeConfigurationDao.deleteById(idToDelete);
    }

    public StudentPasswordChangeConfiguration save(StudentPasswordChangeConfiguration studentPasswordChangeConfiguration) {
        return studentPasswordChangeConfigurationDao.save(studentPasswordChangeConfiguration);
    }
}
