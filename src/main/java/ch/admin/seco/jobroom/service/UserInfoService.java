package ch.admin.seco.jobroom.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.CompanyContactTemplate;
import ch.admin.seco.jobroom.domain.CompanyId;
import ch.admin.seco.jobroom.domain.StesInformation;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.IsAdmin;
import ch.admin.seco.jobroom.security.registration.eiam.EiamAdminService;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.service.dto.AccountabilityDTO;
import ch.admin.seco.jobroom.service.dto.CompanyContactTemplateDTO;
import ch.admin.seco.jobroom.service.dto.StesInformationDto;
import ch.admin.seco.jobroom.service.dto.UserInfoDTO;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserInfoService {

    private final UserInfoRepository userInfoRepository;

    private final EiamAdminService eiamAdminService;

    public UserInfoService(UserInfoRepository userInfoRepository, EiamAdminService eiamAdminService) {
        this.userInfoRepository = userInfoRepository;
        this.eiamAdminService = eiamAdminService;
    }

    @IsAdmin
    public UserInfoDTO getUserInfo(String eMail) throws UserInfoNotFoundException {
        Optional<UserInfo> userInfo = this.userInfoRepository.findByEMail(eMail);
        return userInfo.map(UserInfoService::toUserInfoDTO)
            .orElseThrow(() -> new UserInfoNotFoundException(eMail));
    }

    @IsAdmin
    public Set<String> getRoles(String eMail) throws UserNotFoundException {
        return eiamAdminService.getRoles(eMail);
    }

    @PreAuthorize("@userInfoAuthorizationService.isCurrentUser(#userInfoId) or hasAuthority('ROLE_ADMIN')")
    public List<AccountabilityDTO> getAccountabilities(UserInfoId userInfoId) throws UserInfoNotFoundException {
        UserInfo userInfo = getUserInfo(userInfoId);
        return toAccountabilityDTOs(userInfo);
    }

    @PreAuthorize("@userInfoAuthorizationService.isCurrentUser(#userInfoId) or hasAuthority('ROLE_ADMIN')")
    public CompanyContactTemplateDTO getCompanyContactTemplate(UserInfoId userInfoId, CompanyId companyId) throws UserInfoNotFoundException, CompanyContactTemplateNotFoundException {
        UserInfo userInfo = getUserInfo(userInfoId);
        return toCompanyContactTemplateDTO(userInfo.getCompanyContactTemplate(companyId));
    }

    @PreAuthorize("@userInfoAuthorizationService.isCurrentUser(#userInfoId) or hasAuthority('ROLE_ADMIN')")
    public Set<CompanyContactTemplateDTO> getCompanyContactTemplates(UserInfoId userInfoId) throws UserInfoNotFoundException {
        UserInfo userInfo = getUserInfo(userInfoId);
        return toCompanyContactTemplates(userInfo);
    }

    @PreAuthorize("@userInfoAuthorizationService.isCurrentUser(#userInfoId)")
    public void addCompanyContactTemplate(UserInfoId userInfoId, CompanyContactTemplateDTO companyContactTemplateDTO) throws UserInfoNotFoundException {
        UserInfo userInfo = getUserInfo(userInfoId);
        userInfo.addCompanyContactTemplate(toCompanyContactTemplate(companyContactTemplateDTO));
    }

    @PreAuthorize("@userInfoAuthorizationService.isCurrentUser(#userInfoId)")
    public void removeCompanyContactTemplate(UserInfoId userInfoId, CompanyId companyId) throws UserInfoNotFoundException {
        UserInfo userInfo = getUserInfo(userInfoId);
        userInfo.removeContactTemplate(companyId);
    }

    private Set<CompanyContactTemplateDTO> toCompanyContactTemplates(UserInfo userInfo) {
        return userInfo.getCompanyContactTemplates()
            .stream()
            .map(this::toCompanyContactTemplateDTO)
            .collect(Collectors.toSet());
    }

    private CompanyContactTemplateDTO toCompanyContactTemplateDTO(CompanyContactTemplate companyContactTemplate) {
        CompanyContactTemplateDTO contactTemplateDTO = new CompanyContactTemplateDTO();
        contactTemplateDTO.setCompanyId(companyContactTemplate.getCompanyId().getValue());
        contactTemplateDTO.setCompanyName(companyContactTemplate.getCompanyName());
        contactTemplateDTO.setCompanyStreet(companyContactTemplate.getCompanyStreet());
        contactTemplateDTO.setCompanyHouseNr(companyContactTemplate.getCompanyHouseNr());
        contactTemplateDTO.setCompanyZipCode(companyContactTemplate.getCompanyZipCode());
        contactTemplateDTO.setCompanyCity(companyContactTemplate.getCompanyCity());
        contactTemplateDTO.setPhone(companyContactTemplate.getPhone());
        contactTemplateDTO.setEmail(companyContactTemplate.getEmail());
        contactTemplateDTO.setSalutation(companyContactTemplate.getSalutation());
        return contactTemplateDTO;
    }

    private static CompanyContactTemplate toCompanyContactTemplate(CompanyContactTemplateDTO companyContactTemplateDTO) {
        return CompanyContactTemplate.builder()
            .setCompanyId(new CompanyId(companyContactTemplateDTO.getCompanyId()))
            .setCompanyName(companyContactTemplateDTO.getCompanyName())
            .setCompanyStreet(companyContactTemplateDTO.getCompanyStreet())
            .setCompanyHouseNr(companyContactTemplateDTO.getCompanyHouseNr())
            .setCompanyZipCode(companyContactTemplateDTO.getCompanyZipCode())
            .setCompanyCity(companyContactTemplateDTO.getCompanyCity())
            .setPhone(companyContactTemplateDTO.getPhone())
            .setEmail(companyContactTemplateDTO.getEmail())
            .setSalutation(companyContactTemplateDTO.getSalutation())
            .build();
    }


    private UserInfo getUserInfo(UserInfoId userInfoId) throws UserInfoNotFoundException {
        return this.userInfoRepository.findById(userInfoId)
            .orElseThrow(() -> new UserInfoNotFoundException(userInfoId.getValue()));
    }

    private static UserInfoDTO toUserInfoDTO(UserInfo userInfo) {
        return new UserInfoDTO.Builder()
            .setId(userInfo.getId().getValue())
            .setUserExternalId(userInfo.getUserExternalId())
            .setFirstName(userInfo.getFirstName())
            .setLastName(userInfo.getLastName())
            .setEmail(userInfo.getEmail())
            .setRegistrationStatus(userInfo.getRegistrationStatus())
            .setAccountabilities(toAccountabilityDTOs(userInfo))
            .setStesInformation(userInfo.getStesInformation()
                .map(UserInfoService::toStesInformationDto)
                .orElse(null)
            )
            .setCreatedAt(userInfo.getCreatedAt())
            .setModifiedAt(userInfo.getModifiedAt())
            .setLastLoginAt(userInfo.getLastLoginAt())
            .build();
    }


    private static StesInformationDto toStesInformationDto(StesInformation stesInformation) {
        return new StesInformationDto(
            stesInformation.getPersonNumber(),
            stesInformation.getVerificationType(),
            stesInformation.getVerifiedAt()
        );
    }

    private static List<AccountabilityDTO> toAccountabilityDTOs(UserInfo userInfo) {
        return userInfo.getAccountabilities().stream()
            .map(accountability -> new AccountabilityDTO(
                accountability.getType(),
                accountability.getCompany().getId().getValue(),
                accountability.getCompany().getName(),
                accountability.getCompany().getExternalId(),
                accountability.getCompany().getSource()
            ))
            .collect(Collectors.toList());
    }

}
