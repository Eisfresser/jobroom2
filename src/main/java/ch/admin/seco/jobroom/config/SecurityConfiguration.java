package ch.admin.seco.jobroom.config;

import ch.admin.seco.jobroom.security.saml.AbstractSecurityConfig;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.SamlProperties;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamSamlUserDetailsService;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.CorsFilter;

import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.jwt.JWTConfigurer;
import ch.admin.seco.jobroom.security.jwt.TokenProvider;

import java.util.Map;

import static ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer.saml;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

/*
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserDetailsService userDetailsService;

    private final TokenProvider tokenProvider;

    private final CorsFilter corsFilter;

    private final SecurityProblemSupport problemSupport;

    public SecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService, TokenProvider tokenProvider, CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
       // this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.corsFilter = corsFilter;
        this.problemSupport = problemSupport;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        try {
            return authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
        } catch (Exception e) {
            throw new BeanInitializationException("Security configuration failed", e);
        }
    }

*/



    @Configuration
    //@Order(101)      //TODO: which order?
    @ConfigurationProperties(prefix = "security")
    static class SamlSecurityConfig extends AbstractSecurityConfig {

        //@Autowired
        //private IamService iamService;

        private Map<String, String> rolemapping;

        @Autowired
        private SamlProperties samlProperties;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);

            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .apply(saml())
                .serviceProvider()
                /*-*/.keyStore()
                /*----*/.storeFilePath(samlProperties.getKeystorePath())
                /*----*/.password(samlProperties.getKeystorePassword())
                /*----*/.keyname(samlProperties.getKeystorePrivateKeyName())
                /*----*/.keyPassword(samlProperties.getKeystorePrivateKeyPassword())
                /*----*/.and()
                /*-*/.protocol(samlProperties.getExternalContextScheme())
                /*-*/.hostname(samlProperties.getExternalContextServerName() + ":" + samlProperties.getExternalContextServerPort())
                /*-*/.basePath(samlProperties.getExternalContextPath())
                /*-*/.entityId(samlProperties.getEntityId())
                /*-*/.entityAlias(samlProperties.getEntityAlias())
                /*-*/.withEmptyStorage(false)
                /*-*/.excludeCredential(false)
                .and()
                .identityProvider()
                /*-*/.discoveryEnabled(false)
                /*-*/.signMetadata(true)
                /*-*/.metadataFilePath(samlProperties.getIdpConfigPath())
                .and()
                .userDetailsService(this.eiamSamlUserDetailsService());
        }

        //TODO: create these Objects as beans/services to inject them..
        private EiamSamlUserDetailsService eiamSamlUserDetailsService() {
            return new EiamSamlUserDetailsService(samlBasedUserDetailsProvider());
        }

        private SamlBasedUserDetailsProvider samlBasedUserDetailsProvider() {
            return new DefaultSamlBasedUserDetailsProvider(rolemapping);
        }

        public Map<String, String> getRolemapping() {
            return rolemapping;
        }

        public void setRolemapping(Map<String, String> rolemapping) {
            this.rolemapping = rolemapping;
        }
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }

/*
    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
*/
}
