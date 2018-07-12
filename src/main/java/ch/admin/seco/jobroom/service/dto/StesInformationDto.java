package ch.admin.seco.jobroom.service.dto;

import java.time.LocalDateTime;

import ch.admin.seco.jobroom.domain.StesVerificationType;

public class StesInformationDto {

    private Long personNumber;

    private StesVerificationType verificationType;

    private LocalDateTime verifiedAt;

    public StesInformationDto(Long personNumber, StesVerificationType verificationType, LocalDateTime verifiedAt) {
        this.personNumber = personNumber;
        this.verificationType = verificationType;
        this.verifiedAt = verifiedAt;
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
}
