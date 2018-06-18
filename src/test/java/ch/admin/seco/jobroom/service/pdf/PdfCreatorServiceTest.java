package ch.admin.seco.jobroom.service.pdf;

import ch.admin.seco.jobroom.JobroomApp;
import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

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
        registeringUser = new UserInfo("Hans","Muster","hans.muster@example.com","extId",Constants.DEFAULT_LANGUAGE);
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
