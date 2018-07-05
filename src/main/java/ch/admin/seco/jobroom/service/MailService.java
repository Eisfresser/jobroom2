package ch.admin.seco.jobroom.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Stream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import io.github.jhipster.config.JHipsterProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.IDNEmailAddressConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;
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

    private PdfCreatorService pdfCreatorService;

    private IDNEmailAddressConverter idnEmailAddressConverter;

    public MailService(JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine,
        PdfCreatorService pdfCreatorService) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.pdfCreatorService = pdfCreatorService;
        this.idnEmailAddressConverter = new IDNEmailAddressConverter();
    }

    @Deprecated
    public void sendActivationEmail(User user) {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mails/activationEmail", "email.activation.title");
    }

    @Deprecated
    public void sendCreationEmail(User user) {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mails/creationEmail", "email.activation.title");
    }

    @Deprecated
    public void sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mails/passwordResetEmail", "email.reset.title");
    }

    public void sendAccessCodeLetterMail(String emailAddress, UserInfo userInfo) {
        log.debug("Sending access code letter email to the service desk '{}'", emailAddress);
        String attachmentFilename = "Zugriffscode_Brief.pdf";
        String pathToPdf = generatePdf(userInfo);
        Locale locale = Locale.forLanguageTag("de");
        Context context = new Context(locale);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        context.setVariable("userEmail", userInfo.getEmail());
        String content = templateEngine.process("mails/accessCodeLetterEmail", context);
        String subject = messageSource.getMessage("email.accessCodeLetter.title", null, locale);
        sendEmail(emailAddress, subject, content, true, true, attachmentFilename, pathToPdf);
    }

    public void sendAnonymousContactMail(AnonymousContactMessageDTO anonymousContactMessage, String recipient) {
        log.debug("Sending anonymous contact email to '{}'", recipient);
        Context context = createAnonymousContactMailContext(anonymousContactMessage);
        String content = templateEngine.process("mails/anonymousContactEmail", context);
        String subject = messageSource.getMessage("email.anonymousContact.mail-subject", null, LocaleContextHolder.getLocale());
        sendEmail(recipient, subject, content, false, true);
    }

    @Deprecated
    void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        sendEmail(to, subject, content, isMultipart, isHtml, null, null);
    }

    private void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml, String attachmentFilename, String attachmentFilePath) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(idnEmailAddressConverter.toASCII(to));
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
        } catch (MessagingException e) {
            throw new MailSendException("Could not send email", e);
        }
    }

    private String generatePdf(UserInfo userInfo) {
        try {
            return pdfCreatorService.createAccessCodePdf(userInfo);
        } catch (IOException e) {
            throw new IllegalStateException("The access code letter for the user " + userInfo.getFirstName() + " " + userInfo.getLastName() + " could not be generated.", e);
        }
    }

    private Context createAnonymousContactMailContext(AnonymousContactMessageDTO anonymousContactMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        Context context = new Context(locale);
        context.setVariable("subject", anonymousContactMessage.getSubject());
        context.setVariable("body", anonymousContactMessage.getBody());
        context.setVariable("companyName", anonymousContactMessage.getCompanyName());
        context.setVariable("phone", anonymousContactMessage.getPhone());
        context.setVariable("email", anonymousContactMessage.getEmail());
        setCompanyData(context, anonymousContactMessage.getCompany());
        return context;
    }

    private void setCompanyData(Context context, AnonymousContactMessageDTO.CompanyDTO company) {
        if (company == null) {
            context.setVariable("hasCompany", false);
            return;
        }
        context.setVariable("hasCompany", true);
        context.setVariable("name", company.getName());
        context.setVariable("contactPerson", company.getContactPerson());
        context.setVariable("address", prepareCompanyAddress(company));
        context.setVariable("city", prepareCompanyCity(company));
        context.setVariable("country", company.getCountry());
    }

    private String prepareCompanyAddress(AnonymousContactMessageDTO.CompanyDTO company) {
        return joinStrings(company.getStreet(), company.getHouseNumber());
    }

    private String prepareCompanyCity(AnonymousContactMessageDTO.CompanyDTO company) {
        return joinStrings(company.getZipCode(), company.getCity());
    }

    private String joinStrings(String... values) {
        return StringUtils.join(Stream.of(values).filter(StringUtils::isNotEmpty).toArray(), ", ");
    }
}
