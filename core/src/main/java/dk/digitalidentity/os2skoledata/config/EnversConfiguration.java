package dk.digitalidentity.os2skoledata.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = { "dk.digitalidentity.os2skoledata.dao" }, repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class)
public class EnversConfiguration {

}