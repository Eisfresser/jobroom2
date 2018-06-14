package ch.admin.seco.jobroom.security.registration.stes;

import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(name = "candidateservice", decode404 = true)
public interface StesService {

    @PostMapping("/api/candidates/verify")
    StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody RegisterJobseekerVM jobseekerDetails);

}
