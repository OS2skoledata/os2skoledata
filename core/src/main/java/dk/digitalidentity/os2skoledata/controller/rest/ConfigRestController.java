package dk.digitalidentity.os2skoledata.controller.rest;

import dk.digitalidentity.os2skoledata.dao.model.PasswordSetting;
import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import dk.digitalidentity.os2skoledata.dao.model.enums.RoleSettingType;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
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

import java.util.List;

@RequireAdministratorRole
@RestController
public class ConfigRestController {

    @Autowired
    private StudentPasswordChangeConfigurationService studentPasswordChangeConfigurationService;

    @Autowired
    private PasswordSettingService passwordSettingService;

    @DeleteMapping("/rest/config/{id}/delete")
    public ResponseEntity<?> deleteConfig(@PathVariable long id) {
        studentPasswordChangeConfigurationService.delete(id);
        return ResponseEntity.ok().build();
    }

    private record SaveClientDTO(long id, StudentPasswordChangerSTILRoles role, RoleSettingType type, List<String> constraints) {}
    @ResponseBody
    @PostMapping("/rest/config/save")
    public ResponseEntity<?> saveConfig(@RequestBody SaveClientDTO dto) {
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
        PasswordSetting match = passwordSettingService.getSettings(dto.gradeGroup());
        match.setMinLength(dto.minLength());
        match.setRequireComplexPassword(dto.requireComplexPassword());
        passwordSettingService.save(match);

        return new ResponseEntity<>(passwordSettingService.getAllPasswordSettings(), HttpStatus.OK);
    }
}
