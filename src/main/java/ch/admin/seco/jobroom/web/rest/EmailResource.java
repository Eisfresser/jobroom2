package ch.admin.seco.jobroom.web.rest;

import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.service.CandidateService;
import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;

@RestController
@RequestMapping("/api")
public class EmailResource {

    private final Logger log = LoggerFactory.getLogger(EmailResource.class);

    private final CandidateService candidateService;

    private final MailService mailService;

    public EmailResource(CandidateService candidateService, MailService mailService) {
        this.candidateService = candidateService;
        this.mailService = mailService;
    }

    @PostMapping("/messages/send-anonymous-message")
    @Timed
    public ResponseEntity<Object> sendAnonymousContactMessage(@RequestBody AnonymousContactMessageDTO anonymousContactMessage) {
        log.debug("REST request to send anonymous contact message : {}", anonymousContactMessage);
        return candidateService.getCandidate(anonymousContactMessage.getCandidateId())
            .map(candidateProtectedDataDto -> {
                mailService.sendAnonymousContactMail(anonymousContactMessage, candidateProtectedDataDto.getEmail());
                return ResponseEntity.accepted().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
