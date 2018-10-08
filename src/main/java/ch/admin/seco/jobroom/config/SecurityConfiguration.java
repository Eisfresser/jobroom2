package ch.admin.seco.jobroom.config;

import static ch.admin.seco.jobroom.security.saml.infrastructure.dsl.SAMLConfigurer.saml;
import static org.opensaml.saml2.core.AuthnContext.KERBEROS_AUTHN_CTX;
import static org.opensaml.saml2.core.AuthnContext.NOMAD_TELEPHONY_AUTHN_CTX;
import static org.opensaml.saml2.core.AuthnContext.SMARTCARD_PKI_AUTHN_CTX;
import static org.opensaml.saml2.core.AuthnContext.SOFTWARE_PKI_AUTHN_CTX;

import java.util.Arrays;
import java.util.Collection;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;
import org.apache.commons.lang.StringUtils;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.filter.CorsFilter;

import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.LoginFormUserDetailsService;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.jwt.JWTConfigurer;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.SamlAuthenticationFailureHandler;
import ch.admin.seco.jobroom.security.saml.SamlAuthenticationSuccessHandler;
import ch.admin.seco.jobroom.security.saml.SamlProperties;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamSamlUserDetailsService;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    @Configuration
    @Profile("no-eiam")
    static class NoEiamSecurityConfig extends AbstractSecurityConfig {

        private final LoginFormUserDetailsService loginFormUserDetailsService;

        private final JHipsterProperties jHipsterProperties;

        private final CorsFilter corsFilter;

        private final SecurityProblemSupport problemSupport;

        NoEiamSecurityConfig(LoginFormUserDetailsService loginFormUserDetailsService, CorsFilter corsFilter, SecurityProblemSupport problemSupport, JHipsterProperties jHipsterProperties) {
            super(problemSupport);
            this.loginFormUserDetailsService = loginFormUserDetailsService;
            this.jHipsterProperties = jHipsterProperties;
            this.corsFilter = corsFilter;
            this.problemSupport = problemSupport;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new MD5PasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager() throws Exception {
            return super.authenticationManager();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
            auth.userDetailsService(this.loginFormUserDetailsService)
                .passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/api/activate").permitAll()
                .antMatchers("/api/account/reset-password/init").permitAll()
                .antMatchers("/api/account/reset-password/finish").permitAll();

            http
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .and()
                .csrf()
                .disable()
                .headers()
                .cacheControl().disable()
                .addHeaderWriter(prepareHeaderWriter())
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .apply(jwt());

            super.configure(http);
        }

        private HeaderWriter prepareHeaderWriter() {
            RequestMatcher notResourcesMatcher = new NegatedRequestMatcher(new AntPathRequestMatcher("/*service/**"));
            return new DelegatingRequestMatcherHeaderWriter(notResourcesMatcher, new CacheControlHeadersWriter());
        }

        private JWTConfigurer jwt() {
            final Jwt jwt = this.jHipsterProperties.getSecurity()
                .getAuthentication()
                .getJwt();
            return new JWTConfigurer(jwt);
        }
    }

    @Configuration
    @Profile("!no-eiam")
    @EnableConfigurationProperties(EiamSecurityProperties.class)
    static class SamlSecurityConfig extends AbstractSecurityConfig {

        private static final Collection<String> DEFAULT_AUTHN_CTX = Arrays.asList(
            NOMAD_TELEPHONY_AUTHN_CTX,
            SMARTCARD_PKI_AUTHN_CTX,
            SOFTWARE_PKI_AUTHN_CTX,
            KERBEROS_AUTHN_CTX
        );

        private final UserInfoRepository userInfoRepository;

        private final SamlProperties samlProperties;

        private final TransactionTemplate transactionTemplate;

        private final JHipsterProperties jHipsterProperties;

        private final LoginFormUserDetailsService loginFormUserDetailsService;

        private final SecurityProblemSupport problemSupport;

        private final ApplicationEventPublisher applicationEventPublisher;

        private final EiamSecurityProperties eiamSecurityProperties;

        @Autowired
        SamlSecurityConfig(UserInfoRepository userInfoRepository, SamlProperties samlProperties, TransactionTemplate transactionTemplate, JHipsterProperties jHipsterProperties, LoginFormUserDetailsService loginFormUserDetailsService, SecurityProblemSupport problemSupport, ApplicationEventPublisher applicationEventPublisher, EiamSecurityProperties eiamSecurityProperties) {
            super(problemSupport);
            this.userInfoRepository = userInfoRepository;
            this.samlProperties = samlProperties;
            this.transactionTemplate = transactionTemplate;
            this.jHipsterProperties = jHipsterProperties;
            this.loginFormUserDetailsService = loginFormUserDetailsService;
            this.problemSupport = problemSupport;
            this.applicationEventPublisher = applicationEventPublisher;
            this.eiamSecurityProperties = eiamSecurityProperties;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new MD5PasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager() throws Exception {
            return super.authenticationManager();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            super.configure(auth);
            auth
                .userDetailsService(this.loginFormUserDetailsService)
                .passwordEncoder(passwordEncoder());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/samllogin").fullyAuthenticated();

            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .csrf().disable()
                .apply(jwt())
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
                /*-*/.hostname(buildHostname())
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
                .userDetailsService(this.eiamSamlUserDetailsService())
                .successHandler(this.authenticationSuccessHandler())
                .failureHandler(this.authenticationFailureHandler())
                .logoutHandler(this.successLogoutHandler())
                .xmlHttpRequestedWithEntryPoint(this.problemSupport)
                .applicationEventPublisher(this.applicationEventPublisher)
                .defaultAuthnCtx(DEFAULT_AUTHN_CTX);

            super.configure(http);
        }

        private SamlAuthenticationSuccessHandler authenticationSuccessHandler() {
            SamlAuthenticationSuccessHandler authenticationSuccessHandler = new SamlAuthenticationSuccessHandler(
                this.samlProperties.getAccessRequestUrl(),
                this.userInfoRepository,
                this.authenticationEventPublisher()
            );
            authenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(true);
            authenticationSuccessHandler.setDefaultTargetUrl("/");
            return authenticationSuccessHandler;
        }

        private AuthenticationEventPublisher authenticationEventPublisher() {
            return new DefaultAuthenticationEventPublisher();
        }

        private SamlAuthenticationFailureHandler authenticationFailureHandler() {
            return new SamlAuthenticationFailureHandler(this.eiamSecurityProperties.isEnableRedirectOnCancellation());
        }

        private SimpleUrlLogoutSuccessHandler successLogoutHandler() {
            SimpleUrlLogoutSuccessHandler successLogoutHandler = new SimpleUrlLogoutSuccessHandler();
            successLogoutHandler.setDefaultTargetUrl("/");
            return successLogoutHandler;
        }

        private JWTConfigurer jwt() {
            final Jwt jwt = this.jHipsterProperties.getSecurity()
                .getAuthentication()
                .getJwt();
            return new JWTConfigurer(jwt);
        }

        private String buildHostname() {
            if (StringUtils.isBlank(samlProperties.getExternalContextServerPort())) {
                return samlProperties.getExternalContextServerName();
            }
            return samlProperties.getExternalContextServerName() + ":" + samlProperties.getExternalContextServerPort();
        }

        private EiamSamlUserDetailsService eiamSamlUserDetailsService() {
            return new EiamSamlUserDetailsService(samlBasedUserDetailsProvider());
        }

        private SamlBasedUserDetailsProvider samlBasedUserDetailsProvider() {
            return new DefaultSamlBasedUserDetailsProvider(
                this.userInfoRepository,
                this.eiamSecurityProperties.getRolemapping(),
                this.transactionTemplate
            );
        }

    }

}
