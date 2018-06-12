package ch.admin.seco.jobroom.security.registration.eiam.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ch.admin.seco.jobroom.security.registration.eiam.EiamClient;

@Configuration
@Profile("eiam-mock")
public class EiamMockClientConfig {

    @Bean
    public EiamClient eiamClient() {
        return new EiamClientMock();
    }

}
