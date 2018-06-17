package ch.admin.seco.jobroom.service.pdf;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;

public abstract class PdfDocument<T> {

    abstract String create(T data, String creationDirectoryPath) throws IOException;

    static final int LINE_HEIGHT = 18;

    protected PDDocument document = new PDDocument();
    protected PdfCoord currentPosition = new PdfCoord(0, 0);
    protected PDPageContentStream contentStream;

    protected static final PDFont FONT_NORMAL = PDType1Font.HELVETICA;
    protected static final PDFont FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    protected static final PDFont FONT_ITALIC = PDType1Font.HELVETICA_OBLIQUE;
    protected static final PDFont FONT_ITALIC_BOLD = PDType1Font.HELVETICA_BOLD_OBLIQUE;

    protected static final int FONT_SMALL = 10;
    protected static final int FONT_MEDIUM = 12;
    protected static final int FONT_LARGE = 14;

    protected static final Color COLOR_PRIMARY = Color.BLACK;
    protected static final Color COLOR_SECONDARY = Color.GRAY;

    protected final MessageSource messageSource;
    protected final Locale locale;


    PdfDocument(MessageSource messageSource, String languageKey) throws IOException {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(languageKey);
        this.createNewPage();
        this.setFont(FONT_NORMAL, FONT_MEDIUM, COLOR_PRIMARY);
    }

    PdfDocument createNewPage() throws IOException {
        PDPage page = new PDPage();
        document.addPage(page);
        contentStream = new PDPageContentStream(document, page);
        return this;
    }

    int getNumberOfPages() {
        return this.document.getNumberOfPages();
    }

    PdfDocument setPosition(int x, int y) {
        currentPosition.x = x;
        currentPosition.y = y;
        return this;
    }

    int getX() {
        return currentPosition.x;
    }

    PdfDocument setX(int x) {
        currentPosition.x = x;
        return this;
    }

    int getY() {
        return currentPosition.y;
    }

    PdfDocument setY(int y) {
        currentPosition.y = y;
        return this;
    }

    PdfDocument newLine(int customHeight) {
        currentPosition.y = currentPosition.y - customHeight;
        return this;
    }

    PdfDocument newLine() {
        currentPosition.y = currentPosition.y - LINE_HEIGHT;
        return this;
    }

    PdfDocument insertTextLine(String text) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(currentPosition.x, currentPosition.y);
        contentStream.showText(text);
        contentStream.endText();
        return this;
    }

    PdfDocument setFont(PDFont font, int fontSize) throws IOException {
        contentStream.setFont(font, fontSize);
        return this;
    }

    PdfDocument setFont(PDFont font, int size, Color color) throws IOException {
        contentStream.setNonStrokingColor(color);
        return this.setFont(font, size);
    }

    PdfDocument setStandardFont() throws IOException {
        this.setFont(FONT_NORMAL, FONT_MEDIUM, COLOR_PRIMARY);
        return this;
    }

    PdfDocument addDefaultHeader() throws IOException {
        ClassPathResource cp = new ClassPathResource("images/arbeit.swiss_" + locale.toLanguageTag() + ".png");
        if (!cp.exists()) {
            cp = new ClassPathResource("images/arbeit.swiss_de.png");
        }
        BufferedImage awtImage = ImageIO.read(cp.getInputStream());
        PDImageXObject pdImage = LosslessFactory.createFromImage(this.document, awtImage);
        contentStream.drawImage(pdImage, 380, 700, 140, 88);
        return this.setPosition(385, 680)
            .setFont(FONT_NORMAL, FONT_SMALL, COLOR_SECONDARY)
            .insertTextLine(this.messageSource.getMessage("pdf.header.name", null, this.locale))
            .newLine(12)
            .insertTextLine(this.messageSource.getMessage("pdf.header.street", null, this.locale))
            .newLine(12)
            .insertTextLine(this.messageSource.getMessage("pdf.header.city", null, this.locale));
    }

    String getText() throws IOException {
        contentStream.close();
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        return pdfTextStripper.getText(this.document);
    }

    String saveDocument(String filepath) throws IOException {
        contentStream.close();
        document.save(filepath);
        document.close();
        return filepath;
    }

    public class PdfCoord {
        int x;
        int y;

        PdfCoord(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
