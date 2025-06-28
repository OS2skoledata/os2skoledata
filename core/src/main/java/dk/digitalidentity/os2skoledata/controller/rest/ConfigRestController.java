package dk.digitalidentity.os2skoledata.controller.rest;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.ClassroomAdmin;
import dk.digitalidentity.os2skoledata.dao.model.PasswordAdmin;
import dk.digitalidentity.os2skoledata.dao.model.PasswordSetting;
import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import dk.digitalidentity.os2skoledata.dao.model.enums.RoleSettingType;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.ClassroomAdminService;
import dk.digitalidentity.os2skoledata.service.InstitutionService;
import dk.digitalidentity.os2skoledata.service.PasswordAdminService;
import dk.digitalidentity.os2skoledata.service.PasswordSettingService;
import dk.digitalidentity.os2skoledata.service.StudentPasswordChangeConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RequireAdministratorRole
@RestController
public class ConfigRestController {

    @Autowired
    private StudentPasswordChangeConfigurationService studentPasswordChangeConfigurationService;

    @Autowired
    private PasswordSettingService passwordSettingService;

    @Autowired
    private ClassroomAdminService classroomAdminService;

    @Autowired
    private PasswordAdminService passwordAdminService;

    @Autowired
    private OS2SkoleDataConfiguration configuration;

    @Autowired
    private InstitutionService institutionService;

    @DeleteMapping("/rest/config/{id}/delete")
    public ResponseEntity<?> deleteConfig(@PathVariable long id) {
        if (!configuration.getStudentAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        studentPasswordChangeConfigurationService.delete(id);
        return ResponseEntity.ok().build();
    }

    private record SaveClientDTO(long id, StudentPasswordChangerSTILRoles role, RoleSettingType type, List<String> constraints) {}
    @ResponseBody
    @PostMapping("/rest/config/save")
    public ResponseEntity<?> saveConfig(@RequestBody SaveClientDTO dto) {
        if (!configuration.getStudentAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        StudentPasswordChangeConfiguration match = studentPasswordChangeConfigurationService.findById(dto.id());
        if (match == null) {
            match = new StudentPasswordChangeConfiguration();
            match.setRole(dto.role);
        }

        match.setType(dto.type);
        match.setFilter(String.join(",", dto.constraints));

        studentPasswordChangeConfigurationService.save(match);

        return ResponseEntity.ok().build();
    }

    private record SavePasswordSettingsDTO(GradeGroup gradeGroup, long minLength, boolean requireComplexPassword) {}
    @ResponseBody
    @PostMapping("/rest/config/passwordsettings/save")
    public ResponseEntity<?> savePasswordSettings(@RequestBody SavePasswordSettingsDTO dto) {
        if (!configuration.getStudentAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        PasswordSetting match = passwordSettingService.getSettings(dto.gradeGroup());
        match.setMinLength(dto.minLength());
        match.setRequireComplexPassword(dto.requireComplexPassword());
        passwordSettingService.save(match);

        return new ResponseEntity<>(passwordSettingService.getAllPasswordSettings(), HttpStatus.OK);
    }

    @DeleteMapping("/rest/config/classroomadmin/{id}/delete")
    public ResponseEntity<?> deleteClassroomAdmin(@PathVariable long id) {
        if (!configuration.getClassroomAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        classroomAdminService.delete(id);
        return ResponseEntity.ok().build();
    }

    private record ClassroomAdminDTO(StudentPasswordChangerSTILRoles role, String username) {}
    @ResponseBody
    @PostMapping("/rest/config/classroomadmin/save")
    public ResponseEntity<?> saveClassroomAdmin(@RequestBody ClassroomAdminDTO dto) {
        if (!configuration.getClassroomAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        ClassroomAdmin classroomAdmin = new ClassroomAdmin();
        classroomAdmin.setRole(dto.role);
        classroomAdmin.setUsername(dto.username);
        classroomAdminService.save(classroomAdmin);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/rest/config/passwordadmin/{id}/delete")
    public ResponseEntity<?> deletePasswordadmin(@PathVariable long id) {
        if (!configuration.getStudentAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        passwordAdminService.delete(id);
        return ResponseEntity.ok().build();
    }

    private record PasswordAdminDTO(String username, List<Long> institutionIds) {}
    @ResponseBody
    @PostMapping("/rest/config/passwordadmin/save")
    public ResponseEntity<?> savePasswordadmin(@RequestBody PasswordAdminDTO dto) {
        if (!configuration.getStudentAdministration().isEnabled()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        if (dto.username == null || dto.institutionIds == null || dto.institutionIds.isEmpty()) {
            return new ResponseEntity<>("Brugernavn skal udfyldes og institutioner skal vælges", HttpStatus.BAD_REQUEST);
        }

        if (passwordAdminService.getByUsername(dto.username) != null) {
            return new ResponseEntity<>("Der findes allerede en IT-kodeordsadministrator med dette brugernavn", HttpStatus.BAD_REQUEST);
        }

        PasswordAdmin passwordAdmin = new PasswordAdmin();
        passwordAdmin.setUsername(dto.username);
        passwordAdmin.setInstitutions(new ArrayList<>());
        passwordAdmin.getInstitutions().addAll(institutionService.findByIdIn(dto.institutionIds));
        passwordAdminService.save(passwordAdmin);

        return ResponseEntity.ok().build();
    }
}
