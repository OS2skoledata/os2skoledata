package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.controller.mvc.enums.SchoolClassType;
import dk.digitalidentity.os2skoledata.controller.mvc.enums.SchoolYear;
import dk.digitalidentity.os2skoledata.dao.model.StudentPasswordChangeConfiguration;
import dk.digitalidentity.os2skoledata.dao.model.enums.GradeGroup;
import dk.digitalidentity.os2skoledata.dao.model.enums.RoleSettingType;
import dk.digitalidentity.os2skoledata.dao.model.enums.StudentPasswordChangerSTILRoles;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.PasswordSettingService;
import dk.digitalidentity.os2skoledata.service.StudentPasswordChangeConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequireAdministratorRole
@Controller
public class ConfigController {

    @Autowired
    private StudentPasswordChangeConfigurationService studentPasswordChangeConfigurationService;

    @Autowired
    private PasswordSettingService passwordSettingService;

    @Autowired
    private OS2SkoleDataConfiguration configuration;

    record RoleSettingDTO(long id, StudentPasswordChangerSTILRoles role, RoleSettingType type, String filter, String filterValues) {}
    @GetMapping("/ui/config")
    public String list(Model model) {
        if (!configuration.getStudentAdministration().isEnabled()) {
            return "redirect:/error";
        }

        // who can change password on students section
        List<StudentPasswordChangeConfiguration> configs = studentPasswordChangeConfigurationService.findAll();
        model.addAttribute("roles", StudentPasswordChangerSTILRoles.values());
        model.addAttribute("types", RoleSettingType.values());
        model.addAttribute("classTypes", SchoolClassType.values());
        model.addAttribute("years", SchoolYear.values());
        model.addAttribute("roleSettings", transformFilterMessage(configs));
        model.addAttribute("usedRoles", configs.stream().map(c -> c.getRole()).collect(Collectors.toList()));

        // password settings section
        model.addAttribute("gradeGroups", GradeGroup.values());
        model.addAttribute("passwordSettings", passwordSettingService.getAllPasswordSettings());

        return "config";
    }

    private List<RoleSettingDTO> transformFilterMessage(List<StudentPasswordChangeConfiguration> settings) {
        List<RoleSettingDTO> roleSettingDTOS = new ArrayList<>();

        for (StudentPasswordChangeConfiguration setting : settings) {
            String filterString = "";
            if (setting.getFilter() != null && !setting.getFilter().isEmpty()) {
                if (setting.getType().equals(RoleSettingType.CAN_CHANGE_PASSWORD_ON_GROUP_MATCH)) {
                    List<String> filterClassTypes = Arrays.asList(setting.getFilter().split(","));
                    filterString = filterClassTypes.stream().map(f -> SchoolClassType.valueOf(f).getMessage()).collect(Collectors.joining(", "));
                } else if (setting.getType().equals(RoleSettingType.CAN_CHANGE_PASSWORD_ON_LEVEL_MATCH)) {
                    List<String> filterYears = Arrays.asList(setting.getFilter().split(","));
                    filterString = filterYears.stream().map(f -> SchoolYear.valueOf(f).getMessage()).collect(Collectors.joining(", "));
                }
            }

            RoleSettingDTO dto = new RoleSettingDTO(setting.getId(), setting.getRole(), setting.getType(), filterString, setting.getFilter());
            roleSettingDTOS.add(dto);
        }

        return roleSettingDTOS;
    }
}
