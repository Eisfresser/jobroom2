package ch.admin.seco.jobroom.web.rest;

import javax.validation.Valid;

import com.codahale.metrics.annotation.Timed;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.config.Constants;
import ch.admin.seco.jobroom.security.registration.NoValidPrincipalException;
import ch.admin.seco.jobroom.security.registration.PersistenceException;
import ch.admin.seco.jobroom.security.registration.RegistrationService;
import ch.admin.seco.jobroom.security.registration.StesServiceException;
import ch.admin.seco.jobroom.security.registration.UserAlreadyExistsException;
import ch.admin.seco.jobroom.security.registration.eiam.exceptions.RoleCouldNotBeAddedException;
import ch.admin.seco.jobroom.security.registration.uid.dto.FirmData;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.CompanyNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidClientException;
import ch.admin.seco.jobroom.security.registration.uid.exceptions.UidNotUniqueException;
import ch.admin.seco.jobroom.service.dto.RegistrationResultDTO;
import ch.admin.seco.jobroom.web.rest.vm.LoginVM;
import ch.admin.seco.jobroom.web.rest.vm.RegisterJobseekerVM;

/**
 * Controller to provide all functionality needed during the user registration in Jobroom.
 */
@RestController
@RequestMapping("/api")
//@Profile("!no-eiam")
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
     * @exception NoValidPrincipalException the user seems to be authenticated, but the session does not contain a valid principal
     * @exception UserAlreadyExistsException    the current user cannot be registered, because it's userExternalId already exists in the database
     * @exception RoleCouldNotBeAddedException  adding the role to the eIAM (via eIAM web service call) failed (see error message for details)
     * @exception StesServiceException   user could not be found in the AVG list
     * @param jobseekerDetails  personal number and birthdate entered by the user
     * @return <code>true</code> it the jobseeker was registered successfully, otherwise <code>false</code>
     */
    @PostMapping("/registerJobseeker")
    @Timed
    public boolean registerJobseeker(@Valid @RequestBody RegisterJobseekerVM jobseekerDetails) throws NoValidPrincipalException, UserAlreadyExistsException, RoleCouldNotBeAddedException, StesServiceException {
        if (this.registrationService.validatePersonNumber(jobseekerDetails)) {
            this.registrationService.insertNewJobseeker();
            return true;
        } else {
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
     * @param uid   uid register id of the selected company for which the user should be registered
     * @exception UidClientException    an execption occured during the uid webservice call or the return value is unknown
     * @exception NoValidPrincipalException the user seems to be authenticated, but the session does not contain a valid principal
     * @exception UserAlreadyExistsException    the current user cannot be registered, because it's userExternalId already exists in the database
     * @exception CompanyNotFoundException  the given uid was not found in the uid register
     * @exception UidNotUniqueException the uid register webservice returned more than one result for the given uid
     * @exception PersistenceException  the employer or company entity could not be saved in the database
     */
    @PostMapping("/requestEmployerAccessCode")
    @Timed
    public void requestEmployerAccessCode(@Valid @RequestBody Long uid) throws UidClientException, NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, UidNotUniqueException, PersistenceException {
        this.registrationService.insertNewEmployer(uid);
    }

    /**
     * Requests the company with the given id from the UID register.
     * @param uid   uid register id entered in the frontend
     * @exception UidClientException    an execption occured during the uid webservice call or the return value is unknown
     * @exception CompanyNotFoundException  the given uid was not found in the uid register
     * @exception UidNotUniqueException the uid register webservice returned more than one result for the given uid
     * @return  DTO with the firm data of the company found in the UID register
     */
    @PostMapping("/getCompanyByUid")
    public FirmData getCompanyByUid(@Valid @RequestBody long uid) throws UidClientException, UidNotUniqueException, CompanyNotFoundException {
        return this.registrationService.getCompanyByUid(uid);
    }

    /**
     * Generate a new access code for an agent and store it together with the user in
     * the Jobroom database. This includes the following logic: Create user, find or
     * create company and link to user, create access code, set the user's status to
     * 'waiting for access code' and save user with all this information in the Jobroom
     * database. After that generate letter & send service desk mail with letter attached.
     * At the end of this, the user is shown a message, that access code was ordered.
     * @param avgId avg id of the company selected by the user
     * @exception NoValidPrincipalException the user seems to be authenticated, but the session does not contain a valid principal
     * @exception UserAlreadyExistsException    the current user cannot be registered, because it's userExternalId already exists in the database
     * @exception CompanyNotFoundException  the company with the given avg id was not found in the Organization database
     * @exception PersistenceException  the employer or company entity could not be saved in the database
     */
    @PostMapping("/requestAgentAccessCode")
    @Timed
    public void requestAgentAccessCode(@Valid @RequestBody String avgId) throws NoValidPrincipalException, UserAlreadyExistsException, CompanyNotFoundException, PersistenceException {
        this.registrationService.insertNewAgent(avgId);
    }

    /**
     * Validate the entered access code against the one saved in the database. If it
     * matches, the Employer/Agent role is added to the user (depending on the status of
     * the user VALIDATION_EMP/VALIDATION_PAV). Finally we enforce 2-factor
     * authentication for this user because it is mandatory for company users.
     * @param accessCode the access code entered by the user
     * @return the result object contains a success flag which is <code>true</code> if the employer/agent was registered successfully, otherwise <code>false</code>
     *         and it marks the user type as EMPLOYER or AGENT so that the client can lead the user to the apropriate landing page
     * @exception NoValidPrincipalException the user seems to be authenticated, but the session does not contain a valid principal
     * @exception RoleCouldNotBeAddedException  adding the role to the eIAM (via eIAM web service call) failed (see error message for details)
     */
    @PostMapping("/registerEmployerOrAgent")
    @Timed
    public RegistrationResultDTO registerEmployerOrAgent(@Valid @RequestBody String accessCode) throws NoValidPrincipalException, RoleCouldNotBeAddedException {
        if (this.registrationService.validateAccessCode(accessCode)) {
            return this.registrationService.registerEmployerOrAgent();
        }
        return new RegistrationResultDTO(false, Constants.TYPE_UNKOWN);
    }

    /**
     * Validate the entered access code against the one saved in the database. If it
     * matches, the Employer role is added to the user. Finally we enforce 2-factor
     * authentication for this user because it is mandatory for company users.
     * @param loginData username and password entered by the user
     * @return <code>true</code> it the agent was registered successfully, otherwise <code>false</code>
     * @exception NoValidPrincipalException the user seems to be authenticated, but the session does not contain a valid principal
     * @exception RoleCouldNotBeAddedException  adding the role to the eIAM (via eIAM web service call) failed (see error message for details)
     */
    @PostMapping("/registerExistingAgent")
    @Timed
    public boolean registerExistingAgent(@Valid @RequestBody LoginVM loginData) throws NoValidPrincipalException, RoleCouldNotBeAddedException {
        if (this.registrationService.validateOldLogin(loginData.getUsername(), loginData.getPassword())) {
            this.registrationService.registerExistingAgent();
            //TODO: check 2-factor and send auth request if needed; otherwise send to job admin page
            return true;
        }
        return false;
    }

}
