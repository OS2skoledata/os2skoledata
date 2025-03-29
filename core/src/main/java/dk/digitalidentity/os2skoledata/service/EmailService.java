package dk.digitalidentity.os2skoledata.service;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.service.model.TransportErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service
public class EmailService {

	@Autowired
	private OS2SkoleDataConfiguration configuration;

	public boolean sendMessage(String email, String subject, String message) {
		Transport transport = null;
		log.info("Sending email: '" + subject + "' to " + email);

		try {
			Properties props = System.getProperties();
			props.put("mail.transport.protocol", "smtps");
			props.put("mail.smtp.port", 465);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
			Session session = Session.getDefaultInstance(props);

			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(configuration.getEmail().getFrom(),
					configuration.getEmail().getFrom()));
			msg.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
			msg.setSubject(subject, "UTF-8");
			msg.setText(message, "UTF-8");
			msg.setHeader("Content-Type", "text/html; charset=UTF-8");

			transport = session.getTransport();
			transport.connect(configuration.getEmail().getHost(),
					configuration.getEmail().getUsername(),
					configuration.getEmail().getPassword());
			transport.addTransportListener(new TransportErrorHandler());
			transport.sendMessage(msg, msg.getAllRecipients());
		} catch (Exception ex) {
			log.error("Failed to send email to '" + email + "'", ex);

			return false;
		} finally {
			try {
				if (transport != null) {
					transport.close();
				}
			} catch (Exception ex) {
				log.warn("Error occured while trying to terminate connection", ex);
			}
		}

		return true;
	}

}