package ch.admin.seco.jobroom.web.rest;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;

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
        return new CurrentUserResource.CurrentUserDTO(
            userInfo1.getId().getValue(),
            principal.getEmail(),
            principal.getFirstName(),
            principal.getLastName(),
            principal.getEmail(),
            principal.getLangKey(),
            principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet())
        );
    }
}
