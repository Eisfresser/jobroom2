package ch.admin.seco.jobroom.security;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.jobroom.domain.User;
import ch.admin.seco.jobroom.domain.UserInfo;
import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.repository.UserRepository;

@Component("userDetailsService")
public class LoginFormUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(LoginFormUserDetailsService.class);

    private final UserRepository userRepository;

    private final UserInfoRepository userInfoRepository;

    public LoginFormUserDetailsService(UserRepository userRepository, UserInfoRepository userInfoRepository) {
        this.userRepository = userRepository;
        this.userInfoRepository = userInfoRepository;
    }

    @Override
    @Transactional
    public UserPrincipal loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        User user = getUser(login);
        UserInfo userInfo = findOrCreateUserInfo(user);
        UserPrincipal userPrincipal = new UserPrincipal(
            userInfo.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getLogin(),
            user.getLangKey()
        );
        userPrincipal.setPassword(user.getPassword());
        userPrincipal.setAuthorities(toAuthorities(user));
        userPrincipal.setAccountEnabled(user.getActivated());
        return userPrincipal;
    }

    private User getUser(String login) {
        Optional<User> optionalUser = userRepository.findOneWithAuthoritiesByLogin(login.toLowerCase(Locale.ENGLISH));
        return optionalUser.get();
    }

    private UserInfo findOrCreateUserInfo(User user) {
        Optional<UserInfo> userInfo = this.userInfoRepository.findOneByUserExternalId(user.getLogin());
        if (!userInfo.isPresent()) {
            UserInfo userInfo1 = new UserInfo(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getLogin(),
                user.getLangKey()
            );
            return this.userInfoRepository.saveAndFlush(userInfo1);
        } else {
            userInfo.get().loginWithUpdate(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getLangKey()
            );
            return userInfo.get();
        }
    }

    private List<GrantedAuthority> toAuthorities(User user) {
        return user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());
    }
}
