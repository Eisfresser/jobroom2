package ch.admin.seco.jobroom.service;

import java.util.Optional;

import javax.validation.Valid;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import ch.admin.seco.jobroom.config.OAuth2InterceptedFeignConfiguration;
import ch.admin.seco.jobroom.service.dto.CandidateDto;
import ch.admin.seco.jobroom.service.dto.StesVerificationRequest;
import ch.admin.seco.jobroom.service.dto.StesVerificationResult;

@FeignClient(name = "candidateservice", decode404 = true, configuration = OAuth2InterceptedFeignConfiguration.class, primary = false)
public interface CandidateService {

    @PostMapping("/api/candidates/verify")
    StesVerificationResult verifyStesRegistrationData(@Valid @RequestBody StesVerificationRequest stesVerificationRequest);

    @GetMapping("/api/candidates/{id}")
    @HystrixCommand(fallbackMethod = "emptyCandidate")
    Optional<CandidateDto> getCandidate(@PathVariable("id") String id);

    default Optional<CandidateDto> emptyCandidate() {
        return Optional.empty();
    }

}
