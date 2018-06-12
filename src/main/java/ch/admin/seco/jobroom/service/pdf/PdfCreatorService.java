package ch.admin.seco.jobroom.service.pdf;

import java.io.IOException;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import ch.admin.seco.jobroom.domain.UserInfo;

/**
 * Service for generating PDF documents.
 */
@Service
public class PdfCreatorService {

    private final MessageSource messageSource;

    private final String creationDirectoryPath = System.getProperty("java.io.tmpdir");

    public PdfCreatorService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String createAccessCodePdf(UserInfo user) throws IOException {
        return new AccessCodeDocument(this.messageSource, user.getLangKey()).create(user, this.creationDirectoryPath);

    }


}
