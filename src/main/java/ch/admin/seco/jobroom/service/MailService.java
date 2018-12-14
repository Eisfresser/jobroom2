package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.domain.BlacklistedAgent;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;
import ch.admin.seco.jobroom.service.dto.CandidateDto;
import ch.admin.seco.jobroom.service.logging.BusinessLogEvent;
import ch.admin.seco.jobroom.service.pdf.PdfCreatorService;
import io.github.jhipster.config.JHipsterProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.IDNEmailAddressConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.admin.seco.jobroom.service.logging.BusinessLogEventType.CANDIDATE_CONTACT_MESSAGE;

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

    private final CandidateService candidateService;

    private final ApplicationEventPublisher applicationEventPublisher;

    public MailService(JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine,
        PdfCreatorService pdfCreatorService,
        CandidateService candidateService,
        ApplicationEventPublisher applicationEventPublisher) {
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.pdfCreatorService = pdfCreatorService;
        this.candidateService = candidateService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.idnEmailAddressConverter = new IDNEmailAddressConverter();
    }

    @Deprecated
    public void sendActivationEmail(User user) {
        log.info("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mails/activationEmail", "email.activation.title");
    }

    @Deprecated
    public void sendCreationEmail(User user) {
        log.info("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mails/creationEmail", "email.activation.title");
    }

    @Deprecated
    public void sendPasswordResetMail(User user) {
        log.info("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mails/passwordResetEmail", "email.reset.title");
    }

    public void sendAccessCodeLetterMail(String emailAddress, UserInfo userInfo) {
        log.info("Sending access code letter email to the service desk '{}' concerning user '{}'", emailAddress, userInfo.getId());
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

    public void sendAnonymousContactMail(AnonymousContactMessageDTO anonymousContactMessage) throws CandidateNotFoundException {
        String candidateId = anonymousContactMessage.getCandidateId();
        Optional<CandidateDto> candidateDto = this.candidateService.getCandidate(candidateId);
        if (!candidateDto.isPresent()) {
            throw new CandidateNotFoundException(candidateId);
        }
        CandidateDto candidate = candidateDto.get();
        log.info("Sending anonymous contact email to '{}'", candidate.getEmail());
        Context context = createAnonymousContactMailContext(anonymousContactMessage);
        String content = templateEngine.process("mails/anonymousContactEmail", context);
        String subject = messageSource.getMessage("email.anonymousContact.mail-subject", null, LocaleContextHolder.getLocale());
        sendEmail(candidate.getEmail(), subject, content, false, true);
        applicationEventPublisher.publishEvent(BusinessLogEvent.of(CANDIDATE_CONTACT_MESSAGE).withObjectId(candidate.getExternalId()));
    }

    public void sendStesUnregisteringMail(String stesEmail, String recipient) {
        log.info("Send an email for unregister a candidate with email {} to {} ", stesEmail, recipient);
        Locale locale = Locale.forLanguageTag("de");
        Context context = new Context(locale);
        context.setVariable("candidateEmail", stesEmail);
        String content = templateEngine.process("mails/unregisterCandidateEmail", context);
        String subject = messageSource.getMessage("email.unregisterCandidateEmail.mail-subject", null, locale);
        sendEmail(recipient, subject, content, false, true);
    }

    void sendBlacklistedAgentRequestedAccessCodeMail(String recipient, UserInfo userInfo, BlacklistedAgent agent) {
        log.info("Send an email to the servicedesk {} about blacklisted agent requested access code", recipient);
        Locale locale = LocaleContextHolder.getLocale();
        Context context = new Context(locale);
        context.setVariable("userFirstName", userInfo.getFirstName());
        context.setVariable("userLastName", userInfo.getLastName());
        context.setVariable("userEmail", userInfo.getEmail());
        context.setVariable("agentName", agent.getName());
        String content = templateEngine.process("mails/blacklistedAgentEmail", context);
        String subject = messageSource.getMessage("email.blacklistedAgent.access-code.mail-subject", null, locale);
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
            log.info("Successfully sent email to '{}' with subject '{}'", to, subject);
        } catch (MessagingException e) {
            throw new MailSendException("Could not send email", e);
        }
    }

    private String generatePdf(UserInfo userInfo) {
        try {
            return pdfCreatorService.createAccessCodePdf(userInfo);
        } catch (IOException e) {
            throw new IllegalStateException("The access code letter for the user '" + userInfo.getId() + "' could not be generated.", e);
        }
    }

    private Context createAnonymousContactMailContext(AnonymousContactMessageDTO anonymousContactMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        Context context = new Context(locale);
        context.setVariable("subject", anonymousContactMessage.getSubject());
        context.setVariable("personalMessage", StringUtils.defaultIfBlank(anonymousContactMessage.getPersonalMessage(), null));
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
