package ch.admin.seco.jobroom.security.registration.stes;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "candidateservice", decode404 = true)
public interface StesService {

    @PostMapping("/api/candidates/verify")
    StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody StesVerificationRequest jobseekerDetails);

}
