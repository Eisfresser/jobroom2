package ch.admin.seco.jobroom.service.impl.messaging;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.RegistrationService;

class CandidateDeleteEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CandidateDeleteEventConsumer.class);

    private final StesUnregisteringProperties stesUnregisteringProperties;

    private final RegistrationService registrationService;

    private final MailService mailService;

    private final UserInfoRepository userInfoRepository;

    CandidateDeleteEventConsumer(StesUnregisteringProperties unregisteringProperties, RegistrationService registrationService, MailService mailService, UserInfoRepository userInfoRepository) {
        this.stesUnregisteringProperties = unregisteringProperties;
        this.registrationService = registrationService;
        this.mailService = mailService;
        this.userInfoRepository = userInfoRepository;
    }

    @StreamListener(Sink.INPUT)
    void onCandidateDeleteEvent(CandidateDeletedEvent event) throws UserNotFoundException {
        LOGGER.debug("Received CandidateDeletedEvent for Candidate-Id: {}", event.getCandidateId());
        Optional<UserInfo> stesHavingPersonNumber = this.userInfoRepository.findByPersonNumber(event.getPersonNumber());
        if (!stesHavingPersonNumber.isPresent()) {
            LOGGER.info("No registered Stes found having Person-Number: {}", event.getPersonNumber());
            return;
        }
        unregisterStesOrSendEmailToUnregisterStes(stesHavingPersonNumber.map(UserInfo::getEmail).get());
    }

    private void unregisterStesOrSendEmailToUnregisterStes(String email) throws UserNotFoundException {
        if (this.stesUnregisteringProperties.isAutoUnregisteringEnabled()) {
            this.registrationService.unregisterJobSeeker(email);
        } else {
            this.mailService.sendStesUnregisteringMail(email, this.stesUnregisteringProperties.getManualUnregisteringMailReceiver());
        }
    }

}
