package ch.admin.seco.jobroom.web.rest;

import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.service.dto.CurrentUserDTO;

@Component
@Transactional
public class UserInfoBasedCurrentUserMapper implements CurrentUserMapper {

    private final UserInfoRepository userInfoRepository;

    public UserInfoBasedCurrentUserMapper(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    public CurrentUserDTO toCurrentUserResource(UserPrincipal principal) {
        return userInfoRepository.findOneWithAccountabilites(principal.getId())
            .map(userInfo -> new CurrentUserDTO()
                .setId(userInfo.getId().getValue())
                .setLogin(principal.getEmail())
                .setEmail(principal.getEmail())
                .setFirstName(principal.getFirstName())
                .setLastName(principal.getLastName())
                .setLangKey(principal.getLangKey())
                .setAuthorities(principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .setRegistrationStatus(userInfo.getRegistrationStatus()))
            .orElseThrow(() -> new IllegalStateException("No User found"));
    }
}
