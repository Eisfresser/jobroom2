package ch.admin.seco.jobroom.security;

import ch.admin.seco.jobroom.domain.CompanyId;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.domain.UserInfoId;
import ch.admin.seco.jobroom.domain.UserInfoRepository;
import ch.admin.seco.jobroom.service.CurrentUserService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserInfoAuthorizationService {

    private final CurrentUserService currentUserService;

    private final UserInfoRepository userInfoRepository;

    public UserInfoAuthorizationService(CurrentUserService currentUserService, UserInfoRepository userInfoRepository) {
        this.currentUserService = currentUserService;
        this.userInfoRepository = userInfoRepository;
    }

    public boolean isCurrentUser(UserInfoId userInfoId) {
        UserPrincipal principal = this.currentUserService.getPrincipal();
        return principal.getId().equals(userInfoId);
    }

    /**
     * This method will be used by Feature JR2-1216.
     * @param userInfoId UserInfo Object
     * @param companyId CompanyId Objec
     * @return boolean value
     */
    public boolean hasAccountability(UserInfoId userInfoId, CompanyId companyId) {
        UserPrincipal principal = this.currentUserService.getPrincipal();
        Optional<UserInfo> userInfo = userInfoRepository.findById(principal.getId());
        if (!userInfo.isPresent()) {
            return true;
        }

        UserInfo userInfo1 = userInfo.get();
        return userInfo1.hasAccountability(companyId);
    }
}
