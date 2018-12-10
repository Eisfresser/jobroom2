package ch.admin.seco.jobroom.service.dto;

import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class LegalTermsDto {

    private String id;

    @NotNull
    private LocalDate effectiveAt;

    @URL
    @Size(max = 255)
    @NotEmpty
    private String linkDe;

    @URL
    @Size(max = 255)
    @NotEmpty
    private String linkEn;

    @URL
    @Size(max = 255)
    @NotEmpty
    private String linkFr;

    @URL
    @Size(max = 255)
    @NotEmpty
    private String linkIt;

    public String getId() {
        return id;
    }

    public LegalTermsDto setId(String id) {
        this.id = id;
        return this;
    }

    public LocalDate getEffectiveAt() {
        return effectiveAt;
    }

    public LegalTermsDto setEffectiveAt(LocalDate effectiveAt) {
        this.effectiveAt = effectiveAt;
        return this;
    }

    public String getLinkDe() {
        return linkDe;
    }

    public LegalTermsDto setLinkDe(String linkDe) {
        this.linkDe = linkDe;
        return this;
    }

    public String getLinkEn() {
        return linkEn;
    }

    public LegalTermsDto setLinkEn(String linkEn) {
        this.linkEn = linkEn;
        return this;
    }

    public String getLinkFr() {
        return linkFr;
    }

    public LegalTermsDto setLinkFr(String linkFr) {
        this.linkFr = linkFr;
        return this;
    }

    public String getLinkIt() {
        return linkIt;
    }

    public LegalTermsDto setLinkIt(String linkIt) {
        this.linkIt = linkIt;
        return this;
    }
}
