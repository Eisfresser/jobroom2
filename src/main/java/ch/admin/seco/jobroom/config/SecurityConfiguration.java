package ch.admin.seco.jobroom.config;

import static ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer.saml;

import java.util.Map;

import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.saml.AbstractSecurityConfig;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.SamlProperties;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamSamlUserDetailsService;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder();
    }


    @Configuration
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

    /*
    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
    */
}
