package ch.admin.seco.jobroom.config;

import static ch.admin.seco.jobroom.security.saml.dsl.SAMLConfigurer.saml;

import java.util.Map;

import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

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
import ch.admin.seco.jobroom.security.saml.AbstractSecurityConfig;
import ch.admin.seco.jobroom.security.saml.DefaultSamlBasedUserDetailsProvider;
import ch.admin.seco.jobroom.security.saml.SamlProperties;
import ch.admin.seco.jobroom.security.saml.infrastructure.EiamSamlUserDetailsService;
import ch.admin.seco.jobroom.security.saml.infrastructure.SamlBasedUserDetailsProvider;

@Configuration
@EnableWebSecurity
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {

    @Configuration
    @ConfigurationProperties(prefix = "security")
    @Profile("no-eiam")
    static class NoEiamSecurityConfig extends WebSecurityConfigurerAdapter {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;

        private final UserDetailsService userDetailsService;

        private final TokenProvider tokenProvider;

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
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder())
                    .and()
                    .build();
            } catch (Exception e) {
                throw new BeanInitializationException("Security configuration failed", e);
            }
        }

        NoEiamSecurityConfig(AuthenticationManagerBuilder authenticationManagerBuilder, UserDetailsService userDetailsService, TokenProvider tokenProvider, CorsFilter corsFilter, SecurityProblemSupport problemSupport) {
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            this.userDetailsService = userDetailsService;
            this.tokenProvider = tokenProvider;
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
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "security")
    @Profile("!no-eiam")
    static class SamlSecurityConfig extends AbstractSecurityConfig {

        //@Autowired
        //private IamService iamService;

        private Map<String, String> rolemapping;

        @Autowired
        private SamlProperties samlProperties;

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

}
