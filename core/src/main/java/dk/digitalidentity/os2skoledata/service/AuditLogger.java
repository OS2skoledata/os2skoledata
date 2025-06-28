package dk.digitalidentity.os2skoledata.service;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dk.digitalidentity.os2skoledata.dao.AuditLogDao;
import dk.digitalidentity.os2skoledata.dao.model.AuditLog;
import dk.digitalidentity.os2skoledata.dao.model.enums.LogAction;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuditLogger {

	@Autowired
	private AuditLogDao auditLogDao;

	@Autowired
	private OS2SkoleDataConfiguration configuration;
	
	public void changePasswordFailed(String personUsername, String personName, String performerUsername, String performerName, String reason) {
		AuditLog auditLog = new AuditLog();
		auditLog.setLogAction(LogAction.CHANGE_PASSWORD_FAILED);
		auditLog.setDetails("Kodeordsskifte afvist.\n" + reason);
		log(auditLog, personUsername, personName, performerUsername, performerName);
	}
	
	public void studentPasswordChangeByEmployee(String personUsername, String personName, String performerUsername, String performerName) {
		AuditLog auditLog = new AuditLog();
		auditLog.setLogAction(LogAction.CHANGE_PASSWORD);
		auditLog.setDetails("AD-kodeord skiftet på elev af skoleansat");
		log(auditLog, personUsername, personName, performerUsername, performerName);
	}

	public void studentPasswordChangeByParent(String personUsername, String personName, String performerCpr) {
		AuditLog auditLog = new AuditLog();
		auditLog.setLogAction(LogAction.CHANGE_PASSWORD);
		auditLog.setDetails("AD-kodeord skiftet på elev af forældre");
		log(auditLog, personUsername, personName, null, CprService.safeCprSubstring(performerCpr));
	}

	public void loginStudentPasswordChange(String parentCpr) {
		AuditLog auditLog = new AuditLog();
		auditLog.setLogAction(LogAction.LOGIN_PARENT);
		auditLog.setDetails("Forældre/værge personnummer: " + CprService.safeCprSubstring(parentCpr));
		log(auditLog, null, null, null, null);
	}

	private void log(AuditLog auditLog, String personUsername, String personName, String performerUsername, String performerName) {
		auditLog.setIpAddress(getIpAddress());

		auditLog.setPersonUsername(personUsername);
		auditLog.setPersonName(personName);
		auditLog.setPerformerUsername(performerUsername);
		auditLog.setPerformerName(performerName);

		auditLogDao.save(auditLog);
	}

	private static String getIpAddress() {
		String remoteAddr = "";

		HttpServletRequest request = getRequest();
		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}

		return remoteAddr;
	}

	private static HttpServletRequest getRequest() {
		try {
			return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		}
		catch (IllegalStateException ex) {
			return null;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void cleanupLogs() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime tts = now.minusMonths(configuration.getDeleteAuditLogsAfterMonths());
	
		auditLogDao.deleteByTtsBefore(tts);
	}
}
