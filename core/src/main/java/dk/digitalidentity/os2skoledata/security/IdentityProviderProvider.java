package dk.digitalidentity.os2skoledata.security;

import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.samlmodule.model.IdentityProvider;
import dk.digitalidentity.samlmodule.model.SamlIdentityProviderProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IdentityProviderProvider implements SamlIdentityProviderProvider {

	@Autowired
	private OS2SkoleDataConfiguration config;

	@Override
	public List<IdentityProvider> getIdentityProviders() {
		List<IdentityProvider> identityProviders = new ArrayList<>();
		
		IdentityProvider employee = IdentityProvider.builder()
				.entityId(config.getIdp().getEmployee().getEntityId())
				.metadata(config.getIdp().getEmployee().getMetadata())
				.build();
		identityProviders.add(employee);

		IdentityProvider parent = IdentityProvider.builder()
				.entityId(config.getIdp().getParent().getEntityId())
				.metadata(config.getIdp().getParent().getMetadata())
				.build();
		identityProviders.add(parent);
		
		return identityProviders;
	}

	@Override
	public IdentityProvider getByEntityId(String entityId) {
		if (entityId.equals(config.getIdp().getEmployee().getEntityId())) {
			return IdentityProvider.builder()
					.entityId(config.getIdp().getEmployee().getEntityId())
					.metadata(config.getIdp().getEmployee().getMetadata())
					.build();
		}
		else if (entityId.equals(config.getIdp().getParent().getEntityId())) {
			return IdentityProvider.builder()
					.entityId(config.getIdp().getParent().getEntityId())
					.metadata(config.getIdp().getParent().getMetadata())
					.build();
		}

		return null;
	}
}
