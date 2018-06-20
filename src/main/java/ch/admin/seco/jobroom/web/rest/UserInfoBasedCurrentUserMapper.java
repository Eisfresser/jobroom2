package ch.admin.seco.jobroom.web.rest;

import ch.admin.seco.jobroom.domain.Company;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class UserInfoBasedCurrentUserMapper implements CurrentUserMapper {

    private final UserInfoRepository userInfoRepository;

    public UserInfoBasedCurrentUserMapper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public CurrentUserResource.CurrentUserDTO toCurrentUserResource(UserPrincipal principal) {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findOneWithAccountabilites(principal.getId());
        if (!optionalUserInfo.isPresent()) {
            throw new IllegalStateException("No User found");
        }
        UserInfo userInfo1 = optionalUserInfo.get();
        Company company = userInfo1.getCompany();
        return new CurrentUserResource.CurrentUserDTO(
            userInfo1.getId().getValue(),
            principal.getEmail(),
            principal.getFirstName(),
            principal.getLastName(),
            principal.getEmail(),
            principal.getLangKey(),
            principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()),
            company == null ? null : company.getExternalId(),
            company == null ? null : company.getName()
        );
    }
}
