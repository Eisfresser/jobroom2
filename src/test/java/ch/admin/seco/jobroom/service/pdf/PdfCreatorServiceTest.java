package ch.admin.seco.jobroom.service.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobroomApp.class)
public class PdfCreatorServiceTest {

    @Autowired
    private MessageSource messageSource;

    private PdfCreatorService pdfCreatorService;

    private UserInfo registeringUser;

    @Before
    public void setup() {
        pdfCreatorService = new PdfCreatorService(messageSource);

        Company userOrganization = new Company();
        userOrganization.setCity("Dübendorf");
        userOrganization.setStreet("Stadtstrasse 21");
        userOrganization.setZipCode("8600");
        userOrganization.setName("Stellenvermittlung24");
        registeringUser = new UserInfo();
        registeringUser.setAccessCode("CODEXX");
        registeringUser.addCompany(userOrganization);
    }

    @Test
    public void testCreateActivationPdfGerman() throws Exception {
        registeringUser.setLangKey("de");
        String pathToPdf = pdfCreatorService.createAccessCodePdf(registeringUser);
        assertThat(pathToPdf).isEqualTo(System.getProperty("java.io.tmpdir") + "/accessCode_de_CODEXX.pdf");
    }

    @Test
    public void testCreateActivationPdfFrench() throws Exception {
        registeringUser.setLangKey("fr");
        String pathToPdf = pdfCreatorService.createAccessCodePdf(registeringUser);
        assertThat(pathToPdf).isEqualTo(System.getProperty("java.io.tmpdir") + "/accessCode_fr_CODEXX.pdf");
    }

    @Test
    public void testCreateActivationPdfItalian() throws Exception {
        registeringUser.setLangKey("it");
        String pathToPdf = pdfCreatorService.createAccessCodePdf(registeringUser);
        assertThat(pathToPdf).isEqualTo(System.getProperty("java.io.tmpdir") + "/accessCode_it_CODEXX.pdf");
    }

    @Test
    public void testCreateActivationPdfEnglish() throws Exception {
        registeringUser.setLangKey("en");
        String pathToPdf = pdfCreatorService.createAccessCodePdf(registeringUser);
        assertThat(pathToPdf).isEqualTo(System.getProperty("java.io.tmpdir") + "/accessCode_en_CODEXX.pdf");
    }

}
