package ch.admin.seco.jobroom.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.Locale;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import io.github.jhipster.config.JHipsterProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;
import ch.admin.seco.jobroom.service.pdf.PdfCreatorService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobroomApp.class)
public class MailServiceIntTest {

    private static final String MAIL_RECIPIENT = "servicedesk@jobroom.ch";
    private static final String FROM_LOCALHOST = "test@localhost";
    private static final String JOHN_DOE_EMAIL = "john.doe@example.com";

    @Autowired
    private JHipsterProperties jHipsterProperties;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Spy
    private JavaMailSenderImpl javaMailSender;

    @Captor
    private ArgumentCaptor messageCaptor;

    private MailService mailService;

    private PdfCreatorService pdfCreatorService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        pdfCreatorService = new PdfCreatorService(messageSource);
        mailService = new MailService(jHipsterProperties, javaMailSender, messageSource, templateEngine, pdfCreatorService);
    }

    @Test
    public void testSendEmail() throws Exception {
        mailService.sendEmail(JOHN_DOE_EMAIL, "testSubject", "testContent", false, false);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("testSubject");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(JOHN_DOE_EMAIL);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent()).isInstanceOf(String.class);
        assertThat(message.getContent().toString()).isEqualTo("testContent");
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    public void testSendHtmlEmail() throws Exception {
        mailService.sendEmail(JOHN_DOE_EMAIL, "testSubject", "testContent", false, true);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("testSubject");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(JOHN_DOE_EMAIL);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent()).isInstanceOf(String.class);
        assertThat(message.getContent().toString()).isEqualTo("testContent");
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testSendMultipartEmail() throws Exception {
        mailService.sendEmail(JOHN_DOE_EMAIL, "testSubject", "testContent", true, false);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        MimeMultipart mp = (MimeMultipart) message.getContent();
        MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
        ByteArrayOutputStream aos = new ByteArrayOutputStream();
        part.writeTo(aos);
        assertThat(message.getSubject()).isEqualTo("testSubject");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(JOHN_DOE_EMAIL);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent()).isInstanceOf(Multipart.class);
        assertThat(aos.toString()).isEqualTo("\r\ntestContent");
        assertThat(part.getDataHandler().getContentType()).isEqualTo("text/plain; charset=UTF-8");
    }

    @Test
    public void testSendMultipartHtmlEmail() throws Exception {
        mailService.sendEmail(JOHN_DOE_EMAIL, "testSubject", "testContent", true, true);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        MimeMultipart mp = (MimeMultipart) message.getContent();
        MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
        ByteArrayOutputStream aos = new ByteArrayOutputStream();
        part.writeTo(aos);
        assertThat(message.getSubject()).isEqualTo("testSubject");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(JOHN_DOE_EMAIL);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent()).isInstanceOf(Multipart.class);
        assertThat(aos.toString()).isEqualTo("\r\ntestContent");
        assertThat(part.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testSendEmailFromTemplate() throws Exception {
        User user = new User();
        user.setLogin("john");
        user.setEmail(JOHN_DOE_EMAIL);
        user.setLangKey("en");
        mailService.sendEmailFromTemplate(user, "mails/testEmail", "email.test.title");
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("test title");
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent().toString()).contains("<html>test title, http://127.0.0.1:8080, john</html>");
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testSendActivationEmail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail(JOHN_DOE_EMAIL);
        mailService.sendActivationEmail(user);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent().toString()).isNotEmpty();
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testCreationEmail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail(JOHN_DOE_EMAIL);
        mailService.sendCreationEmail(user);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent().toString()).isNotEmpty();
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testSendPasswordResetMail() throws Exception {
        User user = new User();
        user.setLangKey(Constants.DEFAULT_LANGUAGE);
        user.setLogin("john");
        user.setEmail(JOHN_DOE_EMAIL);
        mailService.sendPasswordResetMail(user);
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent().toString()).isNotEmpty();
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testSendAccessCodeLetterMail() throws Exception {
        Company company = new Company();
        company.setCity("Dübendorf");
        company.setStreet("Stadtstrasse 21");
        company.setZipCode("8600");
        company.setName("Stellenvermittlung24");
        UserInfo user = new UserInfo("Hans", "Muster", JOHN_DOE_EMAIL, "extid", Constants.DEFAULT_LANGUAGE);
        user.requestAccessAsEmployer(company);

        mailService.sendAccessCodeLetterMail(MAIL_RECIPIENT, user);

        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(MAIL_RECIPIENT);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(((MimeMultipart) message.getContent()).getCount()).isEqualTo(2);
        assertThat(message.getDataHandler().getContentType().startsWith("multipart/mixed"));
        assertThat(((MimeMultipart) message.getContent()).getBodyPart(1).getContent() instanceof FileInputStream);
    }

    @Test
    public void testSendAnonymousContactMail() throws Exception {
        AnonymousContactMessageDTO anonymousContactMessage = new AnonymousContactMessageDTO();
        anonymousContactMessage.setSubject("Title");
        anonymousContactMessage.setBody("Message body");
        String subject = messageSource.getMessage("email.anonymousContact.mail-subject", null, LocaleContextHolder.getLocale());

        mailService.sendAnonymousContactMail(anonymousContactMessage, MAIL_RECIPIENT);

        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(MAIL_RECIPIENT);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getSubject()).isEqualTo(subject);
    }

    @Test
    public void testSendStesUnregisteringMail() throws Exception {
        //given
        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.unregisterCandidateEmail.mail-subject", null, locale);
        String content = stesUnregisteringMailContext(JOHN_DOE_EMAIL, locale);

        //when
        mailService.sendStesUnregisteringMail(JOHN_DOE_EMAIL, MAIL_RECIPIENT);

        //then
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(MAIL_RECIPIENT);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getSubject()).isEqualTo(subject);
        assertThat(message.getContent()).isEqualTo(content);
    }

    private String stesUnregisteringMailContext(String mail, Locale locale) {
        Context context = new Context(locale);
        context.setVariable("candidateEmail", mail);
        return templateEngine.process("mails/unregisterCandidateEmail", context);
    }
}
