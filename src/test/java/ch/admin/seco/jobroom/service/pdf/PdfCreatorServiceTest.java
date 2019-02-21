package ch.admin.seco.jobroom.service.pdf;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import java.util.Collection;
import java.util.Locale;

import static ch.admin.seco.jobroom.config.Constants.DEFAULT_LANGUAGE;
import static java.util.Arrays.asList;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
@SpringBootTest(classes = JobroomApp.class)
public class PdfCreatorServiceTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Autowired
    private MessageSource messageSource;

    private PdfCreatorService pdfCreatorService;

    private UserInfo registeringUser;
    private Locale locale;
    private String expectedLanguage;

    @Before
    public void setup() {
        pdfCreatorService = new PdfCreatorService(messageSource);

        Company company = new Company();
        company.setCity("Dübendorf");
        company.setStreet("Stadtstrasse 21");
        company.setZipCode("8600");
        company.setName("Stellenvermittlung24");
        registeringUser = new UserInfo("Hans", "Muster", "hans.muster@example.com", "extId", DEFAULT_LANGUAGE);
        registeringUser.requestAccessAsEmployer(company);
    }

    @Parameters(name = "{index}: should create access code PDF of language {1} if locale is {0}")
    public static Collection<Object[]> data() {
        return asList(new Object[][] {
            { GERMANY,"de" },
            { FRANCE, "fr" },
            { ITALY, "it" },
            { US, "en" },
            { forLanguageTag("es"), "de" }
        });
    }

    public PdfCreatorServiceTest(Locale locale, String expectedLanguage) {
        this.locale = locale;
        this.expectedLanguage = expectedLanguage;
    }

    @Test
    public void test() throws Exception {
        LocaleContextHolder.setLocale(locale);
        String accessCode = registeringUser.getAccessCode();

        String pathToPdf = pdfCreatorService.createAccessCodePdf(registeringUser);

        assertThat(pathToPdf).isEqualTo(System.getProperty("java.io.tmpdir") + "/accessCode_" + expectedLanguage + "_" + accessCode + ".pdf");
    }
}
