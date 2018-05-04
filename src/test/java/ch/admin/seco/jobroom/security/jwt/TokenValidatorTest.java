package ch.admin.seco.jobroom.security.jwt;

import org.junit.Before;
import org.junit.Test;

import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static ch.admin.seco.jobroom.security.jwt.TestTokenFactory.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TokenValidatorTest {

    private TokenValidator tokenValidator;

    @Before
    public void setup() {
        tokenValidator = new TokenValidator(e5c9ee274ae87bc031adda32e27fa98b9290da83.name());
    }

    @Test
    public void testReturnFalseWhenJWThasInvalidSignature() {
        boolean tokenValid = tokenValidator.validateToken(TOKEN_WITH_DIFFERENT_SIGNATURE);

        assertThat(tokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisExpired() {
        boolean tokenValid = tokenValidator.validateToken(EXPIRED_TOKEN);

        assertThat(tokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisUnsupported() {
        boolean tokenValid = tokenValidator.validateToken(UNSUPPORTED_TOKEN);

        assertThat(tokenValid).isEqualTo(false);
    }

    @Test
    public void testReturnFalseWhenJWTisInvalid() {
        boolean tokenValid = tokenValidator.validateToken(EMPTY_TOKEN);

        assertThat(tokenValid).isEqualTo(false);
    }
}
