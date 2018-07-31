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

    private static final int ADDRESS_LEFT_BORDER = 60;
    private static final int CONTENT_LEFT_BORDER = 90;
    private static final int TOP_BORDER = 685;

    private static final int ADDRESS_BOTTOM_MARGIN = 100;
    private static final int PLACE_AND_DATE_BOTTOM_MARGIN = 50;
    private static final int TITLE_BOTTOM_MARGIN = 20;
    private static final int SALUTATION_BOTTOM_MARGIN = 20;
    private static final int MESSAGE_BOTTOM_MARGIN = 20;
    private static final int ACCESS_CODE_BOTTOM_MARGIN = 20;
    private static final int INFO_BOTTOM_MARGIN = 20;
    private static final int TIP_BOTTOM_MARGIN = 20;
    private static final int PROPS_BOTTOM_MARGIN = 40;
    private static final int NOTICE_BOTTOM_MARGIN = 25;
    private static final int FAREWELL_BOTTOM_MARGIN = 25;

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
            .addDefaultFooter()
            .setPosition(ADDRESS_LEFT_BORDER, TOP_BORDER);

        this.addressHead()
            .address(user)
            .placeAndDate()
            .title()
            .salutation()
            .message()
            .applicant(user)
            .accessCode(user)
            .info()
            .tip()
            .props()
            .notice()
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
            .setFont(FONT_BOLD, FONT_SMALL, COLOR_PRIMARY)
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

    private AccessCodeDocument addressHead() throws IOException {
        return (AccessCodeDocument) this
            .setFont(FONT_BOLD, FONT_SMALL, COLOR_PRIMARY)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.originator.place", null, this.locale))
            .setFont(FONT_NORMAL, FONT_SMALL, COLOR_PRIMARY)
            .setX(ADDRESS_LEFT_BORDER + ((this.locale.toLanguageTag().equals("fr") || this.locale.toLanguageTag().equals("it")) ? 57 : 52))
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.originator.seco", null, this.locale))
            .drawLine(ADDRESS_LEFT_BORDER, 683, ADDRESS_LEFT_BORDER + ((this.locale.toLanguageTag().equals("fr") || this.locale.toLanguageTag().equals("it")) ? 108 : 104), 683)
            .setX(ADDRESS_LEFT_BORDER)
            .newLine(20)

            .setFont(FONT_BOLD, FONT_MEDIUM, COLOR_PRIMARY)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.express", null, this.locale))
            .newLine(14);
    }

    private AccessCodeDocument address(UserInfo user) throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(user.getCompany().getName())
            .newLine(14)
            .insertTextLine(format("%s %s", user.getFirstName(), user.getLastName()))
            .newLine(14)
            .insertTextLine(user.getCompany().getStreet())
            .newLine(14)
            .insertTextLine(format("%s %s", user.getCompany().getZipCode(), user.getCompany().getCity()))
            .newLine(ADDRESS_BOTTOM_MARGIN)
            .setX(CONTENT_LEFT_BORDER);
    }

    private AccessCodeDocument title() throws IOException {
        return (AccessCodeDocument) this
            .setFont(FONT_BOLD, FONT_MEDIUM, COLOR_PRIMARY)
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
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine2", null, this.locale))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine3", null, this.locale))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine4", null, this.locale));

        String messageLine5 = this.messageSource.getMessage("pdf.accesscode.messageLine5", null, this.locale);
        if (!messageLine5.isEmpty()) {
            this.newLine()
                .insertTextLine(messageLine5);
        }
        String messageLine6 = this.messageSource.getMessage("pdf.accesscode.messageLine6", null, this.locale);
        if (!messageLine6.isEmpty()) {
            this.newLine()
                .insertTextLine(messageLine6);
        }
        return (AccessCodeDocument) this
            .newLine(MESSAGE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument applicant(UserInfo user) throws IOException {
        return (AccessCodeDocument) this
            .setX(CONTENT_LEFT_BORDER)
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.applicant", null, this.locale) + ":")
            .setX(CONTENT_LEFT_BORDER + 110)
            .insertTextLine(format("%s %s", user.getFirstName(), user.getLastName()))
            .newLine()

            .setX(CONTENT_LEFT_BORDER)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.email", null, this.locale) + ":")
            .setX(CONTENT_LEFT_BORDER + 110)
            .insertTextLine(user.getEmail())
            .newLine();
    }

    private AccessCodeDocument accessCode(UserInfo user) throws IOException {
        return (AccessCodeDocument) this
            .setX(CONTENT_LEFT_BORDER)
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.accesscode", null, this.locale) + ":")
            .setX(CONTENT_LEFT_BORDER + 110)

            .setMonospaceFont()
            .insertTextLine(user.getAccessCode())

            .setStandardFont()
            .setX(CONTENT_LEFT_BORDER)
            .newLine(ACCESS_CODE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument info() throws IOException {
        return (AccessCodeDocument) this.setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.infoLine1", null, this.locale))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.infoLine2", null, this.locale))
            .newLine(INFO_BOTTOM_MARGIN);
    }

    private AccessCodeDocument tip() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.tipLine1", null, this.locale))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.tipLine2", null, this.locale))
            .newLine(TIP_BOTTOM_MARGIN);
    }

    private AccessCodeDocument props() throws IOException {
        this.setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.propsLine1", null, this.locale))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.propsLine2", null, this.locale));

        String propsLine3 = this.messageSource.getMessage("pdf.accesscode.propsLine3", null, this.locale);
        if (!propsLine3.isEmpty()) {
            this.newLine()
                .insertTextLine(propsLine3);
        }

        return (AccessCodeDocument) this
            .newLine(PROPS_BOTTOM_MARGIN);
    }


    private AccessCodeDocument notice() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.notice", null, this.locale))
            .newLine(NOTICE_BOTTOM_MARGIN);
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
