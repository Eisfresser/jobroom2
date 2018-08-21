package ch.admin.seco.jobroom.web.rest;

import java.util.List;
import java.util.Set;

import com.codahale.metrics.annotation.Timed;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.admin.seco.jobroom.domain.CompanyId;
import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.service.CompanyContactTemplateNotFoundException;
import ch.admin.seco.jobroom.service.RegistrationService;
import ch.admin.seco.jobroom.service.UserInfoNotFoundException;
import ch.admin.seco.jobroom.service.UserInfoService;
import ch.admin.seco.jobroom.service.dto.AccountabilityDTO;
import ch.admin.seco.jobroom.service.dto.CompanyContactTemplateDTO;
import ch.admin.seco.jobroom.service.dto.UserInfoDTO;

@RestController
@RequestMapping("/api/user-info")
public class UserInfoResource {

    private final UserInfoService userInfoService;

    private final RegistrationService registrationService;

    public UserInfoResource(UserInfoService userInfoService, RegistrationService registrationService) {
        this.userInfoService = userInfoService;
        this.registrationService = registrationService;
    }

    @GetMapping("/{eMail}")
    @Timed
    public UserInfoDTO getUserInfo(@PathVariable String eMail) throws UserInfoNotFoundException {
        return this.userInfoService.getUserInfo(eMail);
    }

    @GetMapping("/{eMail}/roles")
    @Timed
    public Set<String> getRoles(@PathVariable String eMail) throws UserNotFoundException {
        return this.userInfoService.getRoles(eMail);
    }

    @GetMapping("/{userInfoId}/accountabilities")
    public List<AccountabilityDTO> getAccountabilities(@PathVariable UserInfoId userInfoId) throws UserInfoNotFoundException {
        return this.userInfoService.getAccountabilities(userInfoId);
    }

    @GetMapping("/{userInfoId}/company-contact-template/{companyId}")
    public CompanyContactTemplateDTO getCompanyContactTemplate(@PathVariable UserInfoId userInfoId, @PathVariable CompanyId companyId) throws UserInfoNotFoundException, CompanyContactTemplateNotFoundException {
        return this.userInfoService.getCompanyContactTemplate(userInfoId, companyId);
    }

    @GetMapping("/{userInfoId}/company-contact-templates")
    public Set<CompanyContactTemplateDTO> getCompanyContactTemplates(@PathVariable UserInfoId userInfoId) throws UserInfoNotFoundException {
        return this.userInfoService.getCompanyContactTemplates(userInfoId);
    }

    @PostMapping("/{userInfoId}/company-contact-templates")
    public void addCompanyContactTemplate(@PathVariable UserInfoId userInfoId, @RequestBody @Validated CompanyContactTemplateDTO companyContactTemplateDTO) throws UserInfoNotFoundException {
        this.userInfoService.addCompanyContactTemplate(userInfoId, companyContactTemplateDTO);
    }

    @DeleteMapping("/{userInfoId}/company-contact-templates/{companyId}")
    public void removeCompanyContactTemplate(@PathVariable UserInfoId userInfoId, @PathVariable CompanyId companyId) throws UserInfoNotFoundException {
        this.userInfoService.removeCompanyContactTemplate(userInfoId, companyId);
    }

    @DeleteMapping("/{eMail}")
    @Timed
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
            case NO_ROLE:
                this.registrationService.unregisterJobSeeker(eMail);
                break;
            default:
                throw new IllegalArgumentException("Unknown type" + role);
        }
    }

    enum Role {
        JOB_SEEKER, PRIVATE_AGENT, COMPANY, NO_ROLE
    }
}
