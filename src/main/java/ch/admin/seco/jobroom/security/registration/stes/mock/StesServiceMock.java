package ch.admin.seco.jobroom.security.registration.stes.mock;

import ch.admin.seco.jobroom.security.registration.stes.StesService;
import ch.admin.seco.jobroom.security.registration.stes.StesVerificationResult;
import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Component
@Profile("stes-mock")
public class StesServiceMock implements StesService {

    @PostMapping("/api/candidates/verify")
    public StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody RegisterJobseekerVM jobseekerDetails) {
        StesVerificationResult stesVerificationResult = new StesVerificationResult();
        stesVerificationResult.setVerified(true);
        return stesVerificationResult;
    }
}
