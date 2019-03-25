package ch.admin.seco.jobroom.web.rest;

import java.time.LocalDate;

import javax.validation.Valid;

import io.micrometer.core.annotation.Timed;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.security.registration.uid.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.UidCompanyNotFoundException;
import ch.admin.seco.jobroom.service.AvgNotFoundException;
import ch.admin.seco.jobroom.service.InvalidAccessCodeException;
import ch.admin.seco.jobroom.service.InvalidPersonenNumberException;
import ch.admin.seco.jobroom.service.RegistrationService;
import ch.admin.seco.jobroom.service.StesPersonNumberAlreadyTaken;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;

/**
 * Controller to provide all functionality needed during the user registration in Jobroom.
 */
@RestController
@RequestMapping("/api")
public class RegistrationController {

    private RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/registerJobseeker")
    @Timed
    public void registerJobseeker(@Valid @RequestBody RegisterJobseekerVM jobseekerDetails) throws InvalidPersonenNumberException, StesPersonNumberAlreadyTaken {
        final LocalDate birthdate = LocalDate.of(jobseekerDetails.getBirthdateYear(), jobseekerDetails.getBirthdateMonth(), jobseekerDetails.getBirthdateDay());
        this.registrationService.registerAsJobSeeker(birthdate, jobseekerDetails.getPersonNumber());
    }

    @PostMapping("/requestEmployerAccessCode")
    @Timed
    public void requestEmployerAccessCode(@Valid @RequestBody Long uid) throws UidCompanyNotFoundException {
        this.registrationService.requestAccessAsEmployer(uid);
    }

    @PostMapping("/getCompanyByUid")
    @Timed
    public FirmData getCompanyByUid(@Valid @RequestBody long uid) throws UidCompanyNotFoundException {
        return this.registrationService.getCompanyByUid(uid);
    }

    @PostMapping("/requestAgentAccessCode")
    @Timed
    public void requestAgentAccessCode(@Valid @RequestBody String avgId) throws AvgNotFoundException {
        this.registrationService.requestAccessAsAgent(avgId);
    }

    @PostMapping("/registerEmployerOrAgent")
    @Timed
    public RegistrationResultDTO registerEmployerOrAgent(@Valid @RequestBody String accessCode) {
        try {
            return this.registrationService.registerAsEmployerOrAgent(accessCode);
        } catch (InvalidAccessCodeException e) {
            return new RegistrationResultDTO(false, Constants.TYPE_UNKOWN);
        }
    }

    @PostMapping("/acceptLegalTerms")
    @Timed
    public void acceptLegalTerms() {
        this.registrationService.acceptLegalTerms();
    }
}
