package ch.admin.seco.jobroom.security.registration.eiam;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("eiam-mock")
public class EiamMockClientConfig {

    @Bean
    public EiamClient eiamClient() {
        return new EiamClientMock();
    }

}
