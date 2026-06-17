package dk.digitalidentity.os2skoledata.service.azure;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.models.PasswordProfile;
import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import dk.digitalidentity.framework.ad.service.model.SetPasswordResponse;
import dk.digitalidentity.os2skoledata.config.OS2SkoleDataConfiguration;
import dk.digitalidentity.os2skoledata.config.modules.AzureAdPasswordConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AzureADPasswordService {
	private final OS2SkoleDataConfiguration configuration;
	private GraphServiceClient graphClient;

	public AzureADPasswordService(OS2SkoleDataConfiguration configuration) {
		this.configuration = configuration;
	}

	public SetPasswordResponse setPassword(String username, String newPassword) {
		SetPasswordResponse response = new SetPasswordResponse();
		response.setStatus(SetPasswordResponse.PasswordStatus.TECHNICAL_ERROR);

		try {
			String upn = buildUpn(username);
			GraphServiceClient client = getGraphClient();

			PasswordProfile passwordProfile = new PasswordProfile();
			passwordProfile.setPassword(newPassword);
			passwordProfile.setForceChangePasswordNextSignIn(false);

			User user = new User();
			user.setPasswordProfile(passwordProfile);

			client.users().byUserId(upn).patch(user);

			response.setStatus(SetPasswordResponse.PasswordStatus.OK);
			response.setMessage("Password changed in Azure AD");
		} catch (Exception ex) {
			log.error("Failed to set password in Azure AD for user: {}", username, ex);
			response.setMessage("Azure AD error: " + ex.getMessage());

			if (ex.getMessage() != null && ex.getMessage().contains("does not exist")) {
				response.setStatus(SetPasswordResponse.PasswordStatus.UNKNOWN_USER);
			}
		}

		return response;
	}

	private String buildUpn(String username) {
		if (username.contains("@")) {
			return username;
		}

		String suffix = configuration.getStudentAdministration().getAzureAd().getUpnSuffix();
		return username + suffix;
	}

	private GraphServiceClient getGraphClient() {
		if (graphClient == null) {
			AzureAdPasswordConfig azureConfig = configuration.getStudentAdministration().getAzureAd();

			ClientSecretCredential credential = new ClientSecretCredentialBuilder()
					.tenantId(azureConfig.getTenantId())
					.clientId(azureConfig.getClientId())
					.clientSecret(azureConfig.getClientSecret())
					.build();

			graphClient = new GraphServiceClient(credential, "https://graph.microsoft.com/.default");
		}

		return graphClient;
	}
}