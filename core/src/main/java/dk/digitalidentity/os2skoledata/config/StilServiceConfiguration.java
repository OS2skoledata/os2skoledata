package dk.digitalidentity.os2skoledata.config;

import https.wsieksport_unilogin_dk.ws.WsiEksport;
import https.wsieksport_unilogin_dk.ws.WsiEksportPortType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.ws.BindingProvider;

@Configuration
public class StilServiceConfiguration {

    @Value("${stil.url:https://wsieksport.unilogin.dk/wsieksport-v6/ws}")
    private String url;

    @Bean
    public WsiEksportPortType caseService7() {
        var wsiEksport = new WsiEksport();
        var wsiEksportPort = wsiEksport.getWsiEksportPort();
        ((BindingProvider) wsiEksportPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        return wsiEksportPort;
    }
}
