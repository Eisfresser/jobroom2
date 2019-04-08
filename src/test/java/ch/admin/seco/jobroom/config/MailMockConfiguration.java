package ch.admin.seco.jobroom.config;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class MailMockConfiguration {

    @MockBean
    JavaMailSender javaMailSender;

}
