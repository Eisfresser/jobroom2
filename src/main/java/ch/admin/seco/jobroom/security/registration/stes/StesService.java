package ch.admin.seco.jobroom.security.registration.stes;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;

@FeignClient(name = "candidateservice", decode404 = true)
public interface StesService {

    @PostMapping("/api/candidates/verify")
    boolean verifyStesRegistrationData(@Valid @RequestBody RegisterJobseekerVM jobseekerDetails);

}
