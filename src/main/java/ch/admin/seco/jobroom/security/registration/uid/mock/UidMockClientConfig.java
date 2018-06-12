package ch.admin.seco.jobroom.security.registration.uid.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import ch.admin.seco.jobroom.security.registration.uid.UidClient;

@Configuration
@Profile("uid-mock")
public class UidMockClientConfig {

    @Bean
    public UidClient uidPublicService() {
        return new UidClientMock();
    }

}
