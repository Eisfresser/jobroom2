package ch.admin.seco.jobroom.security.jwt;

import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;

import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class JWTConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final Jwt jwt;

    public JWTConfigurer(Jwt jwt) {
        this.jwt = jwt;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String secretKey = jwt.getSecret();
        JWTFilter jwtFilter = new JWTFilter(new TokenToAuthenticationConverter(secretKey), TokenResolver.of(secretKey));
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
