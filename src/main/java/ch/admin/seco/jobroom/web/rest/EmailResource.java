package ch.admin.seco.jobroom.web.rest;

import io.micrometer.core.annotation.Timed;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.service.MailService;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;

@RestController
@RequestMapping("/api")
public class EmailResource {

    private MailService mailService;

    public EmailResource(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/messages/send-anonymous-message")
    @Timed
    public ResponseEntity<Void> sendAnonymousContactMessage(@RequestBody AnonymousContactMessageDTO anonymousContactMessage) {
        mailService.sendAnonymousContactMail(anonymousContactMessage);
        return ResponseEntity.accepted().build();
    }
}
