package ch.admin.seco.jobroom.service.impl;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ch.admin.seco.jobroom.service.CandidateService;
import ch.admin.seco.jobroom.service.dto.CandidateDto;
import ch.admin.seco.jobroom.service.dto.StesVerificationRequest;
import ch.admin.seco.jobroom.service.dto.StesVerificationResult;

@Component
@Profile("stes-mock")
@Primary
public class CandidateServiceMock implements CandidateService {

    @PostMapping("/api/candidates/verify")
    @Override
    public StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody StesVerificationRequest stesVerificationRequest) {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(true);
        return stesVerificationResult;
    }

    @Override
    public Optional<CandidateDto> getCandidate(String id) {
        CandidateDto candidateDto = new CandidateDto();
        candidateDto.setId(UUID.fromString(id));
        candidateDto.setEmail("candidate-mail@example.com");
        candidateDto.setFirstName("candidate-firstName");
        candidateDto.setLastName("candidate-lastName");
        candidateDto.setExternalId("AC123456");
        return Optional.of(candidateDto);
    }
}
