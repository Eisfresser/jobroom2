package ch.admin.seco.jobroom.security.jwt;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignJWTRequestInterceptor implements RequestInterceptor {

    private final static Logger LOG = LoggerFactory.getLogger(FeignJWTRequestInterceptor.class);

    private final TokenProvider tokenProvider;

    public FeignJWTRequestInterceptor(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void apply(RequestTemplate template) {
        LOG.debug("About to prepare the JWT token for the Feign request");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String token = this.tokenProvider.createToken(authentication, false);
            LOG.debug("Prepared JWT token for Feign");
            template.header(JWTConfigurer.TokenResolver.AUTHORIZATION_HEADER, JWTConfigurer.TokenResolver.TOKEN_PREFIX + token);
        }
    }
}
