package ch.admin.seco.jobroom.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.URL;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import static java.time.LocalDate.now;
import static java.util.Objects.hash;
import static org.springframework.util.Assert.notNull;

@Entity
public class LegalTerms implements Serializable {

    private static final long serialVersionUID = 1L;

    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Id
    private String id;

    @NotNull
    private LocalDate effectiveAt;

    @URL
    @Size(max = 255)
    @NotNull
    private String linkDe;

    @URL
    @Size(max = 255)
    @NotNull
    private String linkEn;

    @URL
    @Size(max = 255)
    @NotNull
    private String linkFr;

    @URL
    @Size(max = 255)
    @NotNull
    private String linkIt;

    public LegalTerms() {
        // for reflection only
    }

    public LegalTerms(String id) {
        notNull(id, "Id must be set.");
        this.id = id;
    }

    public LegalTerms setId(String id) {
        this.id = id;
        return this;
    }

    public String getId() {
        return id;
    }

    public LocalDate getEffectiveAt() {
        return effectiveAt;
    }

    public LegalTerms setEffectiveAt(LocalDate effectiveAt) {
        this.effectiveAt = effectiveAt;
        return this;
    }

    public String getLinkDe() {
        return linkDe;
    }

    public LegalTerms setLinkDe(String linkDe) {
        this.linkDe = linkDe;
        return this;
    }

    public String getLinkEn() {
        return linkEn;
    }

    public LegalTerms setLinkEn(String linkEn) {
        this.linkEn = linkEn;
        return this;
    }

    public String getLinkFr() {
        return linkFr;
    }

    public LegalTerms setLinkFr(String linkFr) {
        this.linkFr = linkFr;
        return this;
    }

    public String getLinkIt() {
        return linkIt;
    }

    public LegalTerms setLinkIt(String linkIt) {
        this.linkIt = linkIt;
        return this;
    }

    public boolean isFutureEffective() {
        return this.effectiveAt != null && this.effectiveAt.isAfter(now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegalTerms that = (LegalTerms) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return hash(id);
    }

    @Override
    public String toString() {
        return "LegalTerms{" +
            "id='" + id + '\'' +
            ", effectiveAt=" + effectiveAt +
            ", linkDe='" + linkDe + '\'' +
            ", linkEn='" + linkEn + '\'' +
            ", linkFr='" + linkFr + '\'' +
            ", linkIt='" + linkIt + '\'' +
            '}';
    }
}
