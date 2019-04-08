package ch.admin.seco.jobroom.security.registration.uid;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("uid-mock")
public class UidMockClientConfig {

    @Bean
    public UidClient uidPublicService() {
        return new UidClientMock();
    }

}
