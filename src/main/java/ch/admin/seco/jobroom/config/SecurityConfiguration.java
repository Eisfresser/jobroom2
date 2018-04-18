package ch.admin.seco.jobroom.config;

import static ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer.saml;

import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import ch.admin.seco.jobroom.security.AuthoritiesConstants;
import ch.admin.seco.jobroom.security.MD5PasswordEncoder;
import ch.admin.seco.jobroom.security.jwt.JWTConfigurer;
import ch.admin.seco.jobroom.security.jwt.TokenProvider;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamSamlUserDetailsService;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    @Configuration
    @Profile("saml-security")
    static class SamlSecurityConfig extends AbstractSecurityConfig {

        private SamlProperties samlProperties;

        // private final EiamSamlUserDetailsService userDetailsService;

        SamlSecurityConfig(SamlProperties samlProperties, SecurityProblemSupport problemSupport) {
            super(problemSupport);
            this.samlProperties = samlProperties;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {

            // formatter:off
            http
                .headers()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .and()
                    .authorizeRequests()
                        .antMatchers("/api/register").permitAll()
                        .antMatchers("/api/activate").permitAll()
                        .antMatchers("/api/authenticate").permitAll()
                        .antMatchers("/api/account/reset-password/init").permitAll()
                        .antMatchers("/api/account/reset-password/finish").permitAll()
                        .antMatchers("/api/profile-info").permitAll()
                        .antMatchers("/api/**").authenticated()
                        .antMatchers("/management/health").permitAll()
                        .antMatchers("/management/info").permitAll()
                        .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                        .antMatchers("/v2/api-docs/**").permitAll()
                        .antMatchers("/swagger-resources/configuration/ui").permitAll()
                        .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN)
                    .and()
                        .apply(saml())
                            .serviceProvider()
                                .keyStore()
                                    .storeFilePath(samlProperties.getKeystorePath())
                                    .password(samlProperties.getKeystorePassword())
                                    .keyname(samlProperties.getKeystorePrivateKeyName())
                                    .keyPassword(samlProperties.getKeystorePrivateKeyPassword())
                                .and()
                                    .protocol(samlProperties.getExternalContextScheme())
                                    .hostname(samlProperties.getExternalContextServerName() + ":" + samlProperties.getExternalContextServerPort())
                                    .basePath(samlProperties.getExternalContextPath())
                                    .entityId(samlProperties.getEntityId())
                                    //TODO: Do we need this?
                                    //.entityAlias(samlProperties.getEntityAlias())
                                    .withEmptyStorage(false)
                                    .excludeCredential(false)
                            .and()
                                .identityProvider()
                                    .discoveryEnabled(false)
                                    .signMetadata(true)
                                    .metadataFilePath(samlProperties.getIdpConfigPath())
                            .and()
                                .userDetailsService(eiamSamlUserDetailsService());

        }

        private EiamSamlUserDetailsService eiamSamlUserDetailsService() {
            return new EiamSamlUserDetailsService(samlBasedUserDetailsProvider());
        }

        private SamlBasedUserDetailsProvider samlBasedUserDetailsProvider() {
            return new DefaultSamlBasedUserDetailsProvider();
        }

        //TODO: we need to strip out password for users in case saml is active.
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new MD5PasswordEncoder();
        }
    }

    @Configuration
    @Profile("!saml-security")
    static class MockedSecurityConfig extends AbstractSecurityConfig {

        //private static final String DEFAULT_FORM_LOGOUT_URL = "/form-logout";

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final UserDetailsService userDetailsService;
        private final CorsFilter corsFilter;
        private final TokenProvider tokenProvider;



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

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new MD5PasswordEncoder();
        }

        /*@Autowired
        private IamService iamService;
        */
        MockedSecurityConfig(SecurityProblemSupport problemSupport, AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService, CorsFilter corsFilter, TokenProvider tokenProvider) {
            super(problemSupport);
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            this.userDetailsService = userDetailsService;
            this.corsFilter = corsFilter;
            this.tokenProvider = tokenProvider;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);

            http
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .headers()
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
                .antMatchers("/api/profile-info").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/swagger-resources/configuration/ui").permitAll()
                .antMatchers("/swagger-ui/index.html").hasAuthority(AuthoritiesConstants.ADMIN)
                .and()
                .apply(securityConfigurerAdapter());


            /*.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new XmlHttpRequestedWithMatcher())
                .and()
                .formLogin()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher(DEFAULT_FORM_LOGOUT_URL, "GET"))
                .logoutSuccessUrl("/");*/
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }

        /*@Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(new MockedUserDetailsService(this.iamService));
        }*/
    }
}
