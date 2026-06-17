package dk.digitalidentity.os2skoledata.config;

import dk.stil.brugerdatabasen.bpi.wsieksport._7.WsiEksport;
import dk.stil.brugerdatabasen.bpi.wsieksport._7.WsiEksportPortType;
import jakarta.xml.ws.BindingProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.BusFactory;
import org.apache.cxf.ext.logging.LoggingInInterceptor;
import org.apache.cxf.ext.logging.LoggingOutInterceptor;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import java.util.Properties;

@Slf4j
@Configuration
public class StilWsConfiguration {

	@Bean
	public WsiEksportPortType wsiEksportPortType(OS2SkoleDataConfiguration config) {
		BusFactory.getDefaultBus().setProperty("org.apache.cxf.transport.process_fault_on_http_400", true);

		WsiEksportPortType port = new WsiEksport().getWsiEksportPort();
		BindingProvider bindingProvider = (BindingProvider) port;

		// Build signature properties
		Properties signatureProperties = new Properties();
		signatureProperties.put("org.apache.ws.security.crypto.provider",
				"org.apache.wss4j.common.crypto.Merlin");
		signatureProperties.put("org.apache.ws.security.crypto.merlin.keystore.type", "PKCS12");
		signatureProperties.put("org.apache.ws.security.crypto.merlin.keystore.file", config.getStilKeystoreFilePath());
		signatureProperties.put("org.apache.ws.security.crypto.merlin.keystore.password", config.getStilKeystoreFilePW());
		signatureProperties.put("org.apache.ws.security.crypto.merlin.truststore.type", "JKS");
		signatureProperties.put("org.apache.ws.security.crypto.merlin.truststore.file", config.getStilTruststoreFilePath());
		signatureProperties.put("org.apache.ws.security.crypto.merlin.truststore.password", config.getStilTruststoreFilePW());

		// Password callback for signing
		String keystorePassword = config.getStilKeystoreFilePW();
		CallbackHandler passwordCallback = callbacks -> {
			for (Callback callback : callbacks) {
				if (callback instanceof WSPasswordCallback pc) {
					if (pc.getUsage() == WSPasswordCallback.SIGNATURE) {
						pc.setPassword(keystorePassword);
					}
				}
			}
		};

		// Apply security configuration
		bindingProvider.getRequestContext().put(SecurityConstants.SIGNATURE_PROPERTIES, signatureProperties);
		bindingProvider.getRequestContext().put(SecurityConstants.CALLBACK_HANDLER, passwordCallback);
		bindingProvider.getRequestContext().put(SecurityConstants.ASYMMETRIC_SIGNATURE_ALGORITHM, WSConstants.RSA_SHA256);

//		// Enable CXF request/response logging
//		org.apache.cxf.endpoint.Client client = ClientProxy.getClient(port);
//
//		LoggingInInterceptor inInterceptor = new LoggingInInterceptor();
//		inInterceptor.setLimit(-1);
//
//		LoggingOutInterceptor outInterceptor = new LoggingOutInterceptor();
//		outInterceptor.setLimit(-1);
//
//		client.getInInterceptors().add(inInterceptor);
//		client.getOutInterceptors().add(outInterceptor);

		return port;
	}
}