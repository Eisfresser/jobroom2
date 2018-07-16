package ch.admin.seco.jobroom.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.StesInformation;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.IsAdmin;
import ch.admin.seco.jobroom.security.registration.eiam.EiamAdminService;
import ch.admin.seco.jobroom.security.registration.eiam.UserNotFoundException;
import ch.admin.seco.jobroom.service.dto.AccountabilityDTO;
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
                accountability.getCompany().getName(),
                accountability.getCompany().getExternalId(),
                accountability.getCompany().getSource()
            ))
            .collect(Collectors.toList());
    }

}
