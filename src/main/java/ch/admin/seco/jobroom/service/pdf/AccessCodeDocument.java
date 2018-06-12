package ch.admin.seco.jobroom.service.pdf;

import static java.lang.String.format;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.MessageSource;

import ch.admin.seco.jobroom.domain.UserInfo;

public class AccessCodeDocument extends PdfDocument<UserInfo> {

    private final Logger log = LoggerFactory.getLogger(AccessCodeDocument.class);

    private static final int LEFT_BORDER = 80;
    private static final int TOP_BORDER = 650;

    private static final int ADDRESS_BOTTOM_MARGIN = 140;
    private static final int PLACE_AND_DATE_BOTTOM_MARGIN = 50;
    private static final int TITLE_BOTTOM_MARGIN = 50;
    private static final int SALUTATION_BOTTOM_MARGIN = 30;
    private static final int MESSAGE_BOTTOM_MARGIN = 50;
    private static final int ACCESS_CODE_BOTTOM_MARGIN = 50;
    private static final int HELP_NOTICE_BOTTOM_MARGIN = 75;
    private static final int FAREWELL_BOTTOM_MARGIN = 50;

    private static final String GERMAN_DATE_PATTERN = "d. MMMM yyyy";
    private static final String ENGLISH_ITALIAN_DATE_PATTERN = "d MMMM yyyy";
    private static final String FRENCH_DATE_PATTERN = "'le' d MMMM yyyy";

    AccessCodeDocument(MessageSource messageSource, String languageKey) throws IOException {
        super(messageSource, languageKey);
    }

    @Override
    public String create(UserInfo user, String creationDirectoryPath) throws IOException {
        log.debug("Create access code PDF file");

        this.addDefaultHeader()
            .setPosition(LEFT_BORDER, TOP_BORDER);

        this.address(user)
            .placeAndDate()
            .title()
            .salutation()
            .message()
            .accessCode(user)
            .helpNotice()
            .farewell()
            .signature();

        return this.saveDocument(
            format("%s/accessCode_%s_%s.pdf",
                creationDirectoryPath,
                user.getLangKey(),
                user.getAccessCode()
            )
        );
    }

    private AccessCodeDocument placeAndDate() throws IOException {
        LocalDate now = LocalDate.now();
        DateTimeFormatter dateTimeFormatter = getDateFormatter();
        dateTimeFormatter = dateTimeFormatter.withLocale(this.locale);
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(
                format("%s, %s",
                    this.messageSource.getMessage("pdf.accesscode.bern", null, this.locale),
                    now.format(dateTimeFormatter)
                )
            )
            .newLine(PLACE_AND_DATE_BOTTOM_MARGIN);
    }

    private DateTimeFormatter getDateFormatter() {
        switch (this.locale.toLanguageTag()) {
            case "de":
                return DateTimeFormatter.ofPattern(GERMAN_DATE_PATTERN);
            case "fr":
                return DateTimeFormatter.ofPattern(FRENCH_DATE_PATTERN);
            case "it":
            case "en":
                return DateTimeFormatter.ofPattern(ENGLISH_ITALIAN_DATE_PATTERN);
            default:
                return DateTimeFormatter.ofPattern(GERMAN_DATE_PATTERN);
        }
    }

    private AccessCodeDocument address(UserInfo user) throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(user.getCompany().getName())
            .newLine()
            .insertTextLine(user.getCompany().getStreet())
            .newLine()
            .insertTextLine(format("CH-%s %s", user.getCompany().getZipCode(), user.getCompany().getCity()))
            .newLine(ADDRESS_BOTTOM_MARGIN);
    }

    private AccessCodeDocument title() throws IOException {
        return (AccessCodeDocument) this
            .setFont(FONT_BOLD, FONT_LARGE, COLOR_PRIMARY)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.title", null, this.locale))
            .newLine(TITLE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument salutation() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.salutation", null, this.locale))
            .newLine(SALUTATION_BOTTOM_MARGIN);
    }

    private AccessCodeDocument message() throws IOException {
        this.setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine1", null, this.locale))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine2", null, this.locale));

        String messageLine3 = this.messageSource.getMessage("pdf.accesscode.messageLine3", null, this.locale);
        if (!messageLine3.isEmpty()) {
            this.newLine()
                .insertTextLine(messageLine3);
        }
        return (AccessCodeDocument) this
            .newLine(MESSAGE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument accessCode(UserInfo user) throws IOException {
        return (AccessCodeDocument) this
            .setX(LEFT_BORDER + 180)
            .setFont(FONT_ITALIC_BOLD, FONT_LARGE, COLOR_PRIMARY)
            .insertTextLine(user.getAccessCode())
            .setX(LEFT_BORDER)
            .newLine(ACCESS_CODE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument helpNotice() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.helpNotice", null, this.locale))
            .newLine(HELP_NOTICE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument farewell() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.farewell", null, this.locale))
            .newLine(FAREWELL_BOTTOM_MARGIN);
    }

    private AccessCodeDocument signature() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.signature", null, this.locale));
    }

}
