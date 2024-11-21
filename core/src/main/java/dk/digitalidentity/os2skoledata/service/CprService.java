package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.model.CprLookupDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Future;

@Slf4j
@Service
public class CprService {

	@Autowired
	private OS2SkoleDataConfiguration configuration;
	
	@Autowired
	private AuditLogger auditLogger;

	private CprService self;

	@Autowired
	public CprService(@Lazy CprService self) {
		this.self = self;
	}

	@Async
	public Future<CprLookupDTO> getByCpr(String cpr) {
		if (!configuration.getStudentAdministration().isParentPasswordChangeEnabled() || !configuration.getCpr().isEnabled()) {
			if (configuration.getStudentAdministration().isParentPasswordChangeEnabled() && !configuration.getCpr().isEnabled()) {
				log.error("Called method getByCpr, but parent change password on student is enabled and cprService is disabled");
			} else {
				log.warn("Called method getByCpr, but parent change password on student is not enabled or cprService is disabled");
			}

			return null;
		}
		
		RestTemplate restTemplate = new RestTemplate();
		// no reason to lookup invalid cpr numbers
		if (!validCpr(cpr) || isFictionalCpr(cpr)) {
			return null;
		}

		String cprResourceUrl = configuration.getCpr().getUrl();
		if (!cprResourceUrl.endsWith("/")) {
			cprResourceUrl += "/";
		}
		cprResourceUrl += "api/person?cpr=" + cpr + "&cvr=" + configuration.getCvr();

		try {
			ResponseEntity<CprLookupDTO> response = restTemplate.getForEntity(cprResourceUrl, CprLookupDTO.class);
			return new AsyncResult<>(response.getBody());
		}
		catch (IllegalArgumentException ex) {
			log.warn("Failed to lookup: " + safeCprSubstring(cpr), ex);

			return null;
		}
		catch (RestClientResponseException ex) {
			String responseBody = ex.getResponseBodyAsString();

			if (ex.getRawStatusCode() == 404 && responseBody != null && responseBody.contains("PNR not found")) {
				log.warn("Person cpr does not exists in cpr-register: " + safeCprSubstring(cpr));
				
				CprLookupDTO dto = new CprLookupDTO();
				dto.setDoesNotExist(true);
				return new AsyncResult<>(dto);
			}
			else {
				log.warn("Failed to lookup: " + safeCprSubstring(cpr), ex);
			}

			return null;
		}
	}

	public static String safeCprSubstring(String cpr) {
		if (cpr.length() >= 6) {
			return cpr.substring(0, 6) + "-XXXX";
		}
		
		return cpr;
	}
	
	public boolean validCpr(String cpr) {
		if (cpr == null || cpr.length() != 10) {
			return false;
		}
		
		for (char c : cpr.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		
		int days = Integer.parseInt(cpr.substring(0, 2));
		int month = Integer.parseInt(cpr.substring(2, 4));

		if (days < 1 || days > 31) {
			return false;
		}

		if (month < 1 || month > 12) {
			return false;
		}

		return true;
	}
	
	public boolean isFictionalCpr(String cpr) {
		try {
			LocalDate.parse(cpr.substring(0, 6), DateTimeFormatter.ofPattern("ddMMyy"));
		}
		catch (DateTimeParseException ex) {
			return true;
		}

		return false;
	}
}
