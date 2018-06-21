package ch.admin.seco.jobroom.service.pdf;

import ch.admin.seco.jobroom.JobroomApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JobroomApp.class)
public class PdfDocumentTest {

    @Autowired
    private MessageSource messageSourceTest;

    private PdfDocument testPdfDocument;

    @Before
    public void setUp() throws IOException {
        this.testPdfDocument = new PdfDocument<String>(messageSourceTest, "de") {
            @Override
            String create(String data, String creationDirectoryPath) {
                return null;
            }
        };
    }

    @Test
    public void createNewPage() throws IOException {
        assertThat(this.testPdfDocument.getNumberOfPages()).isEqualTo(1);
        this.testPdfDocument.createNewPage();
        assertThat(this.testPdfDocument.getNumberOfPages()).isEqualTo(2);
    }

    @Test
    public void setPosition() {
        this.testPdfDocument.setPosition(100, 200);
        assertThat(this.testPdfDocument.getX()).isEqualTo(100);
        assertThat(this.testPdfDocument.getY()).isEqualTo(200);
    }

    @Test
    public void setX() {
        this.testPdfDocument.setX(300);
        assertThat(this.testPdfDocument.getX()).isEqualTo(300);
    }

    @Test
    public void setY() {
        this.testPdfDocument.setY(400);
        assertThat(this.testPdfDocument.getY()).isEqualTo(400);
    }

    @Test
    public void newLine() {
        int y = this.testPdfDocument.getY();
        this.testPdfDocument.newLine();
        assertThat(this.testPdfDocument.getY()).isEqualTo(y - PdfDocument.LINE_HEIGHT);
    }

    @Test
    public void newLineCustomHeight() {
        int y = this.testPdfDocument.getY();
        this.testPdfDocument.newLine(100);
        assertThat(this.testPdfDocument.getY()).isEqualTo(y - 100);
    }

    @Test
    public void addDefaultHeader() throws IOException {
        this.testPdfDocument.addDefaultHeader();
        assertThat(this.testPdfDocument.getText())
            .contains("Eidgenössisches Departement für")
            .contains("Wirtschaft, Bildung und Forschung WBF")
            .contains("Staatssekretariat für Wirtschaft SECO");
    }

}
