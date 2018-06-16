package ch.admin.seco.jobroom.security.registration.stes.mock;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ch.admin.seco.jobroom.security.registration.stes.StesService;
import ch.admin.seco.jobroom.security.registration.stes.StesVerificationRequest;
import ch.admin.seco.jobroom.security.registration.stes.StesVerificationResult;

@Component
@Profile("stes-mock")
public class StesServiceMock implements StesService {

    @PostMapping("/api/candidates/verify")
    public StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody StesVerificationRequest jobseekerDetails) {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(true);
        return stesVerificationResult;
    }
}
