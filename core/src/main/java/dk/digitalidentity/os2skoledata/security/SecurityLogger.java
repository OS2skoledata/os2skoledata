package dk.digitalidentity.os2skoledata.security;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import dk.digitalidentity.os2skoledata.dao.SecurityLogDao;
import dk.digitalidentity.os2skoledata.dao.model.SecurityLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SecurityLogger {

	@Autowired
	private SecurityLogDao securityLogDao;

	public void log(String ipAddress, String method, String request, int status, long processedTime) {
		if (shouldLog(request)) {
			SecurityLog entry = new SecurityLog();

			if (SecurityUtil.getClient() != null) {
				entry.setClientId(Long.toString(SecurityUtil.getClient().getId()));
				entry.setClientname(SecurityUtil.getClient().getName());
			}
			else {
				log.warn("Failed to identify client during security log!");
				entry.setClientId("UNKNOWN!");
				entry.setClientname("UNKNOWN!");
			}

			entry.setTts(new Date());
			entry.setIpAddress(ipAddress);
			entry.setMethod(method);
			entry.setRequest(request);
			entry.setStatus(status);
			entry.setProcessedTime(processedTime);

			securityLogDao.save(entry);			
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public void cleanupLogs() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date before = cal.getTime();

		securityLogDao.deleteByTimestampBefore(before);
	}

	private boolean shouldLog(String request) {
		return true;

		/* TODO: might need to filter out some calls
		return !(request.startsWith("uri=/api/sync/orgunits") ||
				 request.startsWith("uri=/api/sync/persons") ||
				 request.startsWith("uri=/api/sync/head"));
				 */
	}
}