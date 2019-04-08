package ch.admin.seco.jobroom.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;

@Embeddable
@Access(AccessType.FIELD)
public class StesInformation {

    @NotNull
    private Long personNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StesVerificationType verificationType;

    @NotNull
    private LocalDateTime verifiedAt;

    StesInformation(Long personNumber, StesVerificationType verificationType) {
        this.personNumber = personNumber;
        this.verificationType = verificationType;
        this.verifiedAt = LocalDateTime.now();
    }

    private StesInformation() {
        // FOR JPA
    }

    public Long getPersonNumber() {
        return personNumber;
    }

    public StesVerificationType getVerificationType() {
        return verificationType;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    @Override
    public String toString() {
        return "StesInformation{" +
            "personNumber='" + personNumber + '\'' +
            ", verificationType=" + verificationType +
            ", verifiedAt=" + verifiedAt +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StesInformation that = (StesInformation) o;
        return Objects.equals(personNumber, that.personNumber) &&
            verificationType == that.verificationType &&
            Objects.equals(verifiedAt, that.verifiedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personNumber, verificationType, verifiedAt);
    }
}
