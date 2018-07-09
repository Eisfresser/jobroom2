package ch.admin.seco.jobroom.web.rest;

import java.time.LocalDate;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.security.registration.AvgNotFoundException;
import ch.admin.seco.jobroom.security.registration.InvalidAccessCodeException;
import ch.admin.seco.jobroom.security.registration.InvalidOldLoginException;
import ch.admin.seco.jobroom.security.registration.InvalidPersonenNumberException;
import ch.admin.seco.jobroom.security.registration.RegistrationService;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.UidCompanyNotFoundException;
import ch.admin.seco.jobroom.service.UserInfoNotFoundException;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.service.dto.UserInfoDTO;
import ch.admin.seco.jobroom.web.rest.vm.LoginVM;
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

    /**
     * Register a new Jobseeker.
     * Checks, if the entered pers. no and birthday are valid and if so, create user and
     * attach Jobseeker role in eIAM an in the current session. After that the user is
     * lead to the applications home page.
     *
     * @param jobseekerDetails personal number and birthdate entered by the user
     * @return <code>true</code> it the jobseeker was registered successfully, otherwise <code>false</code>
     */
    @PostMapping("/registerJobseeker")
    @Timed
    public boolean registerJobseeker(@Valid @RequestBody RegisterJobseekerVM jobseekerDetails) {
        try {
            final LocalDate birthdate = LocalDate.of(jobseekerDetails.getBirthdateYear(), jobseekerDetails.getBirthdateMonth(), jobseekerDetails.getBirthdateDay());
            this.registrationService.registerAsJobSeeker(birthdate, jobseekerDetails.getPersonNumber());
            return true;
        } catch (InvalidPersonenNumberException e) {
            return false;
        }
    }

    /**
     * Generate a new access code for an employer and store it together with the user in
     * the Jobroom database. This includes the following logic: Create user, find or
     * create company and link to user, create access code, set the user's status to
     * 'waiting for access code' and save user with all this information in the Jobroom
     * database. After that generate letter & send service desk mail with letter attached.
     * At the end of this, the user is shown a message, that access code was ordered.
     *
     * @param uid uid register id of the selected company for which the user should be registered
     * @throws UidCompanyNotFoundException the given uid was not found in the uid register
     */
    @PostMapping("/requestEmployerAccessCode")
    @Timed
    public void requestEmployerAccessCode(@Valid @RequestBody Long uid) throws UidCompanyNotFoundException {
        this.registrationService.requestAccessAsEmployer(uid);
    }

    /**
     * Requests the company with the given id from the UID register.
     *
     * @param uid uid register id entered in the frontend
     * @return DTO with the firm data of the company found in the UID register
     * @throws UidCompanyNotFoundException the given uid was not found in the uid register
     */
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

    @PostMapping("/registerExistingAgent")
    @Timed
    public boolean registerExistingAgent(@Valid @RequestBody LoginVM loginData) {
        try {
            this.registrationService.registerExistingAgent(loginData.getUsername(), loginData.getPassword());
            return true;
        } catch (InvalidOldLoginException e) {
            return false;
        }
    }

    @GetMapping("/user-info/{eMail}")
    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public UserInfoDTO getUserInfo(@PathVariable String eMail) throws UserInfoNotFoundException {
        return this.registrationService.getUserInfo(eMail);
    }

    @DeleteMapping("/user-info/{eMail}")
    @PreAuthorize("hasAuthority('ROLE_SYSADMIN')")
    public void unregister(@PathVariable String eMail, @RequestParam Role role) throws UserNotFoundException {
        switch (role) {
            case JOB_SEEKER:
                this.registrationService.unregisterJobSeeker(eMail);
                break;
            case PRIVATE_AGENT:
                this.registrationService.unregisterPrivateAgent(eMail);
                break;
            case COMPANY:
                this.registrationService.unregisterCompany(eMail);
                break;
            default:
                throw new IllegalArgumentException("Unknown type" + role);
        }
    }

    enum Role {
        JOB_SEEKER, PRIVATE_AGENT, COMPANY
    }

}
