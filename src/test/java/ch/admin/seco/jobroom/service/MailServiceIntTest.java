package ch.admin.seco.jobroom.service;

import static ch.admin.seco.jobroom.domain.fixture.BlacklistedAgentFixture.testBlacklistedAgent;
import static ch.admin.seco.jobroom.domain.fixture.CompanyFixture.testCompany;
import static ch.admin.seco.jobroom.domain.fixture.UserFixture.testUser;
import static ch.admin.seco.jobroom.domain.fixture.UserInfoFixture.testUserInfo;
import static java.util.Locale.ENGLISH;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.stream.Stream;

import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import ch.admin.seco.jobroom.repository.UserInfoRepository;
import io.github.jhipster.config.JHipsterProperties;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationEventPublisher;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobroom.domain.BlacklistedAgent;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.service.dto.AnonymousContactMessageDTO;
import ch.admin.seco.jobroom.service.pdf.PdfCreatorService;

@RunWith(SpringRunner.class)
@SpringBootTest
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

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private CandidateService candidateService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(javaMailSender).send(any(MimeMessage.class));
        pdfCreatorService = new PdfCreatorService(messageSource);
        mailService = new MailService(jHipsterProperties, javaMailSender, messageSource, templateEngine, pdfCreatorService, candidateService, publisher);
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
        User user = testUser();
        user.setLangKey(ENGLISH.getLanguage());

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
        User user = testUser();

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
        //given
        User user = testUser();

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
        //given
        User user = testUser();

        //when
        mailService.sendPasswordResetMail(user);

        //then
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(user.getEmail());
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getContent().toString()).isNotEmpty();
        assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void testSendAccessCodeLetterMail() throws Exception {
        //given
        Company company = testCompany();
        UserInfo userInfo = testUserInfo();
        userInfo.requestAccessAsEmployer(company);

        //when
        mailService.sendAccessCodeLetterMail(MAIL_RECIPIENT, userInfo);

        //then
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
        //given
        AnonymousContactMessageDTO anonymousContactMessage = new AnonymousContactMessageDTO();
        anonymousContactMessage.setSubject("Title");
        anonymousContactMessage.setPersonalMessage("Message body");
        String subject = messageSource.getMessage("email.anonymousContact.mail-subject", null, getLocale());

        //when
        mailService.sendAnonymousContactMail(anonymousContactMessage);

        //then
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(MAIL_RECIPIENT);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getSubject()).isEqualTo(subject);
    }


    @Test
    public void testSendStesUnregisteringMail() throws Exception {
        //given
        String subject = messageSource.getMessage("email.unregisterCandidateEmail.mail-subject", null, getLocale());
        String content = processIntoContent("mails/unregisterCandidateEmail",
            Pair.of("candidateEmail", JOHN_DOE_EMAIL)
        );

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

    @Test
    public void testSendMailAboutBlacklistedAgent() throws Exception {
        //given
        String subject = messageSource.getMessage("email.blacklistedAgent.access-code.mail-subject", null, getLocale());
        UserInfo userInfo = testUserInfo();
        BlacklistedAgent blacklistedAgent = testBlacklistedAgent().build();
        String content = processIntoContent("mails/blacklistedAgentEmail",
            Pair.of("userFirstName", userInfo.getFirstName()),
            Pair.of("userLastName", userInfo.getLastName()),
            Pair.of("userEmail", userInfo.getEmail()),
            Pair.of("blacklistedAgent", blacklistedAgent)
        );

        //when
        mailService.sendBlacklistedAgentRequestedAccessCodeMail(MAIL_RECIPIENT, userInfo, blacklistedAgent);

        //then
        verify(javaMailSender).send((MimeMessage) messageCaptor.capture());
        MimeMessage message = (MimeMessage) messageCaptor.getValue();
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo(MAIL_RECIPIENT);
        assertThat(message.getFrom()[0].toString()).isEqualTo(FROM_LOCALHOST);
        assertThat(message.getSubject()).isEqualTo(subject);
        assertThat(message.getContent()).isEqualTo(content);
    }

    private String processIntoContent(String template, Pair<String,Object> ... variables) {
        Context context = new Context(getLocale());
        context.setVariables(Stream.of(variables).collect(toMap(Pair::getKey, Pair::getValue)));
        return templateEngine.process(template, context);
    }
}
