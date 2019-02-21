package ch.admin.seco.jobroom.service.pdf;

import ch.admin.seco.jobroom.domain.UserInfo;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
        return new AccessCodeDocument(this.messageSource).create(user, this.creationDirectoryPath);
    }
}
