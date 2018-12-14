package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.service.CandidateNotFoundException;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;

@RestController
@RequestMapping("/api")
public class EmailResource {

    private final Logger log = LoggerFactory.getLogger(EmailResource.class);

    private final MailService mailService;

    public EmailResource(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/messages/send-anonymous-message")
    @Timed
    public void sendAnonymousContactMessage(@Validated @RequestBody AnonymousContactMessageDTO anonymousContactMessage) throws CandidateNotFoundException {
        log.debug("REST request to send anonymous contact message : {}", anonymousContactMessage);
        mailService.sendAnonymousContactMail(anonymousContactMessage);
    }
}
