package ch.admin.seco.jobroom.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.mail.internet.MimeMessage;

import io.github.jhipster.config.JHipsterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;
import ch.admin.seco.jobroom.service.mapper.MailSenderDataMapper;
import ch.admin.seco.jobroom.service.pdf.PdfCreatorService;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JHipsterProperties jHipsterProperties;

    private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;

    private final MailSenderDataMapper mailSenderDataMapper;

    private PdfCreatorService pdfCreatorService;

    public MailService(JHipsterProperties jHipsterProperties, JavaMailSender javaMailSender,
        MessageSource messageSource, SpringTemplateEngine templateEngine,
        MailSenderDataMapper mailSenderDataMapper, PdfCreatorService pdfCreatorService) {

        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.mailSenderDataMapper = mailSenderDataMapper;
        this.pdfCreatorService = pdfCreatorService;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmail(to, subject, content, isMultipart, isHtml, null, null);
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml, String attachmentFilename, String attachmentFilePath) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            if (attachmentFilename != null && attachmentFilePath != null) {
                File attachment = new File(attachmentFilePath);
                if (attachment.exists()) {
                    message.addAttachment(attachmentFilename, attachment);
                }
            }
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);

    }

    @Async
    public void sendEmailFromTemplate(String emailAddress, String templateName, String titleKey, String languageKey) {
        Locale locale = Locale.forLanguageTag(languageKey);
        Context context = new Context(locale);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(emailAddress, subject, content, true, true);
    }

    @Async
    public void sendEmailFromTemplate(String emailAddress, String templateName, String titleKey, String languageKey, String attachmentFilename, String attachmentFilePath) {
        Locale locale = Locale.forLanguageTag(languageKey);
        Context context = new Context(locale);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(emailAddress, subject, content, true, true, attachmentFilename, attachmentFilePath);
    }

    @Async
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "passwordResetEmail", "email.reset.title");
    }

    @Async
    public void sendEmailFromTemplate(MailSenderData mailSenderData, String templateName) {
        Context context = new Context();
        context.setVariables(mailSenderData.getContext());
        String content = templateEngine.process(templateName, context);
        sendEmail(mailSenderData.getTo(), mailSenderData.getSubject(), content, false, true);
    }

    @Async
    public void sendAnonymousContactMail(AnonymousContactMessageDTO anonymousContactMessage) {
        log.debug("Sending anonymous contact email from to '{}'", anonymousContactMessage.getTo());
        final MailSenderData mailSenderData = mailSenderDataMapper.fromAnonymousContactMessageDto(anonymousContactMessage);
        sendEmailFromTemplate(mailSenderData, "anonymousContactEmail");
    }

    @Async
    public void sendAccessCodeLetterMail(String emailAddress, UserInfo userInfo) {
        log.debug("Sending access code letter email to the service desk '{}'", emailAddress);
        String attachmentFilename = "Zugriffscode_Brief.pdf";
        String pathToPdf = null;
        try {
            pathToPdf = pdfCreatorService.createAccessCodePdf(userInfo);
        } catch (IOException e) {
            //TODO: what else should we do? We could send another mail with the user details, so that the SD can manually create the letter or we could send a mail with a link to page, where the letter can be regenerated and manually downloaded
            log.error("The access code letter for the user " + userInfo.getFirstName() + " " + userInfo.getLastName() + " could not be generated.", e);
        }
        sendEmailFromTemplate(emailAddress, "accessCodeLetterEmail", "email.accessCodeLetter.title", "de", attachmentFilename, pathToPdf);
    }

}
