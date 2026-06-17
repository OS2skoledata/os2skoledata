package dk.digitalidentity.os2skoledata.controller.mvc;

import dk.digitalidentity.os2skoledata.dao.model.enums.CustomerSetting;
import dk.digitalidentity.os2skoledata.security.RequireAdministratorRole;
import dk.digitalidentity.os2skoledata.service.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequireAdministratorRole
@Controller
public class MailReportController {

	@Autowired
	private SettingService settingService;

	@GetMapping("/ui/mailreports")
	public String mailReports(Model model) {
		String dailyCreatedEmails = settingService.getStringValueByKey(CustomerSetting.DAILY_CREATED_REPORT_EMAILS);
		model.addAttribute("dailyCreatedEmails", dailyCreatedEmails != null ? dailyCreatedEmails : "");
		return "mail_reports";
	}
}