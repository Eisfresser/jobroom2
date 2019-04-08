package ch.admin.seco.jobroom.service.impl.messaging;

import ch.admin.seco.jobroom.domain.UserInfoRepository;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.RegistrationService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableBinding(Sink.class)
@EnableConfigurationProperties(StesUnregisteringProperties.class)
@Profile("!messagebroker-mock")
public class StesUnregisteringConfig {

    private final StesUnregisteringProperties stesUnregisteringProperties;

    private final RegistrationService registrationService;

    private final MailService mailService;

    private final UserInfoRepository userInfoRepository;

    public StesUnregisteringConfig(
        StesUnregisteringProperties stesUnregisteringProperties,
        RegistrationService registrationService,
        MailService mailService,
        UserInfoRepository userInfoRepository
    ) {
        this.stesUnregisteringProperties = stesUnregisteringProperties;
        this.registrationService = registrationService;
        this.mailService = mailService;
        this.userInfoRepository = userInfoRepository;
    }

    @Bean
    public CandidateDeleteEventConsumer candidateDeleteEventConsumer() {
        return new CandidateDeleteEventConsumer(
            this.stesUnregisteringProperties,
            this.registrationService,
            this.mailService,
            this.userInfoRepository
        );
    }
}
