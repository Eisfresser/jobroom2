package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.domain.StesInformation;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.service.*;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;
import ch.admin.seco.jobroom.service.logging.BusinessLogData;
import ch.admin.seco.jobroom.service.logging.BusinessLogger;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;

import static ch.admin.seco.jobroom.config.Constants.ANONYMOUS_USER;
import static ch.admin.seco.jobroom.security.SecurityUtils.getCurrentUserLogin;
import static ch.admin.seco.jobroom.service.logging.BusinessLogAdditionalKey.USER_LOGIN_ID;
import static ch.admin.seco.jobroom.service.logging.BusinessLogEventType.CANDIDATE_CONTACT_MESSAGE_EVENT;
import static ch.admin.seco.jobroom.service.logging.BusinessLogObjectType.CANDIDATE;
import static java.util.Optional.empty;
import static org.apache.commons.lang.WordUtils.capitalize;

@RestController
@RequestMapping("/api")
public class EmailResource {

    private final Logger log = LoggerFactory.getLogger(EmailResource.class);

    private final UserInfoRepository userInfoRepository;

    private final CandidateService candidateService;

    private final MailService mailService;

    private final BusinessLogger businessLogger;

    public EmailResource(CandidateService candidateService, MailService mailService, BusinessLogger businessLogger, UserInfoRepository userInfoRepository) {
        this.candidateService = candidateService;
        this.mailService = mailService;
        this.businessLogger = businessLogger;
        this.userInfoRepository = userInfoRepository;
    }

    @PostMapping("/messages/send-anonymous-message")
    @Timed
    public ResponseEntity<Object> sendAnonymousContactMessage(@RequestBody AnonymousContactMessageDTO anonymousContactMessage) {
        log.debug("REST request to send anonymous contact message : {}", anonymousContactMessage);
        return candidateService.getCandidate(anonymousContactMessage.getCandidateId())
            .map(candidateProtectedDataDto -> {
                mailService.sendAnonymousContactMail(anonymousContactMessage, candidateProtectedDataDto.getEmail());
                businessLogger.log(prepareBusinessLogDataForCandidateEmail(candidateProtectedDataDto.getEmail()));
                return ResponseEntity.accepted().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private BusinessLogData prepareBusinessLogDataForCandidateEmail(String email) {
        BusinessLogData logData = BusinessLogData.of(CANDIDATE_CONTACT_MESSAGE_EVENT)
            .withObjectType(capitalize(CANDIDATE.name()))
            .withAdditionalData(USER_LOGIN_ID.name(), getCurrentUserLogin().orElse(ANONYMOUS_USER));
        findUserInfoIdByEmail(email).ifPresent(logData::withObjectId);
        return logData;
    }

    private Optional<String> findUserInfoIdByEmail(String email) {
        return userInfoRepository.findByEMail(email)
            .map(userInfo -> userInfo.getStesInformation()
                .map(StesInformation::getPersonNumber)
                .map(Objects::toString))
            .orElse(empty());
    }
}
