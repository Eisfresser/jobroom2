package ch.admin.seco.jobroom.service.pdf;

import ch.admin.seco.jobroom.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.stream.Stream;

import static java.lang.String.format;


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

    private final LanguageTag languageTag;

    private DateTimeFormatter dateTimeFormatter;

    AccessCodeDocument(MessageSource messageSource) throws IOException {
        super(new MessageSourceAccessor(messageSource));
        Locale locale = LocaleContextHolder.getLocale();
        this.languageTag = LanguageTag.determine(locale);
        this.dateTimeFormatter = determineDateFormatter(this.languageTag, locale);
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
                this.languageTag.name().toLowerCase(),
                user.getAccessCode()
            )
        );
    }

    private AccessCodeDocument placeAndDate() throws IOException {
        LocalDate now = LocalDate.now();
        return (AccessCodeDocument) this
            .setFont(FONT_BOLD, FONT_SMALL, COLOR_PRIMARY)
            .insertTextLine(
                format("%s, %s",
                    this.messageSource.getMessage("pdf.accesscode.bern"),
                    now.format(this.dateTimeFormatter)
                )
            )
            .newLine(PLACE_AND_DATE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument addressHead() throws IOException {
        return (AccessCodeDocument) this
            .setFont(FONT_BOLD, FONT_SMALL, COLOR_PRIMARY)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.originator.place"))
            .setFont(FONT_NORMAL, FONT_SMALL, COLOR_PRIMARY)
            .setX(ADDRESS_LEFT_BORDER + ((this.languageTag.equals(LanguageTag.FR) || this.languageTag.equals(LanguageTag.IT)) ? 57 : 52))
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.originator.seco"))
            .drawLine(ADDRESS_LEFT_BORDER, 683, ADDRESS_LEFT_BORDER + ((this.languageTag.equals(LanguageTag.FR) || this.languageTag.equals(LanguageTag.IT)) ? 108 : 104), 683)
            .setX(ADDRESS_LEFT_BORDER)
            .newLine(20)

            .setFont(FONT_BOLD, FONT_MEDIUM, COLOR_PRIMARY)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.express"))
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
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.title"))
            .newLine(TITLE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument salutation() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.salutation"))
            .newLine(SALUTATION_BOTTOM_MARGIN);
    }

    private AccessCodeDocument message() throws IOException {
        this.setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine1"))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine2"))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine3"))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.messageLine4"));

        String messageLine5 = this.messageSource.getMessage("pdf.accesscode.messageLine5");
        if (!messageLine5.isEmpty()) {
            this.newLine()
                .insertTextLine(messageLine5);
        }
        String messageLine6 = this.messageSource.getMessage("pdf.accesscode.messageLine6");
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
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.applicant") + ":")
            .setX(CONTENT_LEFT_BORDER + 110)
            .insertTextLine(format("%s %s", user.getFirstName(), user.getLastName()))
            .newLine()

            .setX(CONTENT_LEFT_BORDER)
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.email") + ":")
            .setX(CONTENT_LEFT_BORDER + 110)
            .insertTextLine(user.getEmail())
            .newLine();
    }

    private AccessCodeDocument accessCode(UserInfo user) throws IOException {
        return (AccessCodeDocument) this
            .setX(CONTENT_LEFT_BORDER)
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.accesscode") + ":")
            .setX(CONTENT_LEFT_BORDER + 110)

            .setMonospaceFont()
            .insertTextLine(user.getAccessCode())

            .setStandardFont()
            .setX(CONTENT_LEFT_BORDER)
            .newLine(ACCESS_CODE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument info() throws IOException {
        return (AccessCodeDocument) this.setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.infoLine1"))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.infoLine2"))
            .newLine(INFO_BOTTOM_MARGIN);
    }

    private AccessCodeDocument tip() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.tipLine1"))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.tipLine2"))
            .newLine(TIP_BOTTOM_MARGIN);
    }

    private AccessCodeDocument props() throws IOException {
        this.setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.propsLine1"))
            .newLine()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.propsLine2"));

        String propsLine3 = this.messageSource.getMessage("pdf.accesscode.propsLine3");
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
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.notice"))
            .newLine(NOTICE_BOTTOM_MARGIN);
    }

    private AccessCodeDocument farewell() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.farewell"))
            .newLine(FAREWELL_BOTTOM_MARGIN);
    }

    private AccessCodeDocument signature() throws IOException {
        return (AccessCodeDocument) this
            .setStandardFont()
            .insertTextLine(this.messageSource.getMessage("pdf.accesscode.signature"));
    }

    private DateTimeFormatter determineDateFormatter(LanguageTag languageTag, Locale locale) {
        switch (languageTag) {
            case DE:
                return DateTimeFormatter.ofPattern(GERMAN_DATE_PATTERN).withLocale(locale);
            case FR:
                return DateTimeFormatter.ofPattern(FRENCH_DATE_PATTERN).withLocale(locale);
            case IT:
            case EN:
                return DateTimeFormatter.ofPattern(ENGLISH_ITALIAN_DATE_PATTERN).withLocale(locale);
            default:
                return DateTimeFormatter.ofPattern(GERMAN_DATE_PATTERN).withLocale(Locale.GERMANY);
        }
    }

    private enum LanguageTag {
        DE, FR, IT, EN;

        static LanguageTag determine(Locale locale) {
            return Stream.of(LanguageTag.values())
                .filter(languageTag -> languageTag.name().toLowerCase().equals(locale.getLanguage()))
                .findFirst()
                .orElse(DE);
        }
    }
}
