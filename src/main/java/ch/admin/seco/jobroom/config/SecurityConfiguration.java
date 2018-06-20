package ch.admin.seco.jobroom.config;

import ch.admin.seco.jobroom.repository.UserInfoRepository;
import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.LoginFormUserDetailsService;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.jwt.JWTConfigurer;
import ch.admin.seco.jobroom.security.saml.AbstractSecurityConfig;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.SamlProperties;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamSamlUserDetailsService;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;
import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.JHipsterProperties.Security.Authentication.Jwt;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.filter.CorsFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.Map;

import static ch.admin.seco.jobroom.security.saml.infrastructure.dsl.SAMLConfigurer.saml;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    @Configuration
    @Profile("no-eiam")
    static class NoEiamSecurityConfig extends WebSecurityConfigurerAdapter {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;

        private final LoginFormUserDetailsService loginFormUserDetailsService;

        private final JHipsterProperties jHipsterProperties;

        private final CorsFilter corsFilter;

        private final SecurityProblemSupport problemSupport;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new MD5PasswordEncoder();
        }


        @Bean
        public AuthenticationManager authenticationManager() {
            try {
                return authenticationManagerBuilder
                    .userDetailsService(loginFormUserDetailsService)
                    .passwordEncoder(passwordEncoder())
                    .and()
                    .build();
            } catch (Exception e) {
                throw new BeanInitializationException("Security configuration failed", e);
            }
        }

        NoEiamSecurityConfig(AuthenticationManagerBuilder authenticationManagerBuilder, LoginFormUserDetailsService loginFormUserDetailsService, CorsFilter corsFilter, SecurityProblemSupport problemSupport, JHipsterProperties jHipsterProperties) {
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            this.loginFormUserDetailsService = loginFormUserDetailsService;
            this.jHipsterProperties = jHipsterProperties;
            this.corsFilter = corsFilter;
            this.problemSupport = problemSupport;
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/app/**/*.{js,html}")
                .antMatchers("/i18n/**")
                .antMatchers("/content/**")
                .antMatchers("/swagger-ui/index.html")
                .antMatchers("/test/**")
                .antMatchers("/h2-console/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            RequestMatcher notResourcesMatcher = new NegatedRequestMatcher(new AntPathRequestMatcher("/*service/**"));
            HeaderWriter notResourcesHeaderWriter = new DelegatingRequestMatcherHeaderWriter(notResourcesMatcher, new CacheControlHeadersWriter());

            // formatter:off
            http
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .csrf()
                .disable()
                .headers()
                .cacheControl().disable()
                .addHeaderWriter(notResourcesHeaderWriter)
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/activate").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/account/reset-password/init").permitAll()
                .antMatchers("/api/account/reset-password/finish").permitAll()
                .antMatchers("/api/active-system-notifications").permitAll()
                .antMatchers("/api/profile-info").permitAll()
                .antMatchers("/api/messages/send-anonymous-message").hasAuthority(AuthoritiesConstants.ROLE_COMPANY)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ROLE_ADMIN)
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll()
                .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ROLE_ADMIN)
                .and()
                .apply(securityConfigurerAdapter());
        }

        private JWTConfigurer securityConfigurerAdapter() {
            final Jwt jwt = this.jHipsterProperties.getSecurity()
                .getAuthentication()
                .getJwt();
            return new JWTConfigurer(jwt);
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "security")
    @Profile("!no-eiam")
    static class SamlSecurityConfig extends AbstractSecurityConfig {

        @Autowired
        private UserInfoRepository userInfoRepository;

        // this is set via @ConfigurationProperties
        private Map<String, String> rolemapping;

        @Autowired
        private SamlProperties samlProperties;

        @Autowired
        private TransactionTemplate transactionTemplate;

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new MD5PasswordEncoder();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);

            http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .apply(saml(samlProperties.getAccessRequestUrl(), this.userInfoRepository))
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
                .userDetailsService(this.eiamSamlUserDetailsService());
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
            return new DefaultSamlBasedUserDetailsProvider(userInfoRepository, rolemapping, this.transactionTemplate);
        }

        public Map<String, String> getRolemapping() {
            return rolemapping;
        }

        public void setRolemapping(Map<String, String> rolemapping) {
            this.rolemapping = rolemapping;
        }
    }
}
