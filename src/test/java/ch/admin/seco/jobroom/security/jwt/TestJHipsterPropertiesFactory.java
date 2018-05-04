package ch.admin.seco.jobroom.security.jwt;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;

import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;

class TestJHipsterPropertiesFactory {

    static final long TOKEN_VALID_60_SECONDS = 60;

    static JHipsterProperties jHipsterProperties() {
        JHipsterProperties jHipsterProperties = new JHipsterProperties();
        Jwt jwt = jHipsterProperties.getSecurity()
                                    .getAuthentication()
                                    .getJwt();
        jwt.setSecret(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
        jwt.setTokenValidityInSeconds(TOKEN_VALID_60_SECONDS);
        return jHipsterProperties;
    }

}
