package ch.admin.seco.jobroom.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.sql.Date;

import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da83;
import static ch.admin.seco.jobroom.security.jwt.TestSecretKey.e5c9ee274ae87bc031adda32e27fa98b9290da90;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static org.apache.commons.lang.StringUtils.EMPTY;

class TestTokenFactory {

    static final String ANONYMOUS_SUBJECT = "anonymous";

    static final String TOKEN_VALID_19_YEARS = "eyJhbGciOiJIUzUxMiJ9.eyJhdXRoIjoiUk9MRV9VU0VSIiwic3ViIjoidGVzdC11c2VyIiwiZXhwIjoyNTI1NDE4ODg1fQ.o6I50xxF3yv9pyPZ2Sm9Mfj-34QFlbd2AP8znSLs-ADmtl13PT5TwIH1rz1Uw33x79amuJSLFKcm4lRG8nKUJA";

    static final String EMPTY_TOKEN = EMPTY;

    static final String INVALID_TOKEN = EMPTY;

    static String TOKEN_WITH_DIFFERENT_SIGNATURE = token(ANONYMOUS_SUBJECT, e5c9ee274ae87bc031adda32e27fa98b9290da90);

    static String EXPIRED_TOKEN = token(ANONYMOUS_SUBJECT, e5c9ee274ae87bc031adda32e27fa98b9290da90);


    static final String UNSUPPORTED_TOKEN = Jwts.builder()
                                                .setPayload("payload")
                                                .signWith(SignatureAlgorithm.HS512, e5c9ee274ae87bc031adda32e27fa98b9290da83.name())
                                                .compact();


    static String token(String subject, TestSecretKey secretKey) {
        return Jwts.builder()
                   .setSubject(subject)
                   .signWith(SignatureAlgorithm.HS512, secretKey.name())
                   .setExpiration(Date.from(now().plusMinutes(1)
                                                 .atZone(systemDefault())
                                                 .toInstant()))
                   .compact();
    }
}

