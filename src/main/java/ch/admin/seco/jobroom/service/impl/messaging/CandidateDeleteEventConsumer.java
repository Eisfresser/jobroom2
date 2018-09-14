package ch.admin.seco.jobroom.service.impl.messaging;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Profile;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.RegistrationService;

@EnableBinding(Sink.class)
@EnableConfigurationProperties(StesUnregistrationProperties.class)
@Profile("!messagebroker-mock")
class CandidateDeleteEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(CandidateDeleteEventConsumer.class);

    private final StesUnregistrationProperties stesUnregistrationProperties;

    private final RegistrationService registrationService;

    private final MailService mailService;

    private final UserInfoRepository userInfoRepository;

    CandidateDeleteEventConsumer(StesUnregistrationProperties ungregistrationProperties, RegistrationService registrationService, MailService mailService, UserInfoRepository userInfoRepository) {
        this.stesUnregistrationProperties = ungregistrationProperties;
        this.registrationService = registrationService;
        this.mailService = mailService;
        this.userInfoRepository = userInfoRepository;
    }

    @StreamListener(Sink.INPUT)
    void onCandidateDeleteEvent(CandidateDeletedEvent event) throws UserNotFoundException {
        LOG.debug("Received an event CandidateDeletedEvent from Kafka");
        Optional<UserInfo> stesHavingPersonNumber = this.userInfoRepository.findByPersonNumber(event.getPersonNumber());
        if (stesHavingPersonNumber.isPresent()) {
            unregisterCandidateOrSendEmailToUnregisterCandidate(stesHavingPersonNumber.map(UserInfo::getEmail).get());
            return;
        }
        LOG.info("No Stes found having Person-Number: {}", event.getPersonNumber());
    }

    private void unregisterCandidateOrSendEmailToUnregisterCandidate(String email) throws UserNotFoundException {
        if (this.stesUnregistrationProperties.isAutoUnregisteringEnabled()) {
            this.registrationService.unregisterJobSeeker(email);
        } else {
            this.mailService.sendStesUnregisteringMail(email, this.stesUnregistrationProperties.getManualUnregisteringMailReceiver());
        }
    }
}
