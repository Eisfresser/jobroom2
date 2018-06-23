package ch.admin.seco.jobroom.service.impl;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ch.admin.seco.jobroom.service.CandidateService;
import ch.admin.seco.jobroom.service.dto.CandidateProtectedDataDto;
import ch.admin.seco.jobroom.service.dto.StesVerificationRequest;
import ch.admin.seco.jobroom.service.dto.StesVerificationResult;

@Component
@Profile("stes-mock")
public class CandidateServiceMock implements CandidateService {

    @PostMapping("/api/candidates/verify")
    @Override
    public StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody StesVerificationRequest stesVerificationRequest) {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(true);
        return stesVerificationResult;
    }

    @Override
    public Optional<CandidateProtectedDataDto> getCandidate(String id) {
        CandidateProtectedDataDto candidateProtectedDataDto = new CandidateProtectedDataDto();
        candidateProtectedDataDto.setId(UUID.fromString(id));
        candidateProtectedDataDto.setEmail("candidate-mail@example.com");
        candidateProtectedDataDto.setFirstName("candidate-firstName");
        candidateProtectedDataDto.setLastName("candidate-lastName");
        return Optional.of(candidateProtectedDataDto);
    }
}
