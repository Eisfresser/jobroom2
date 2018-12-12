package ch.admin.seco.jobroom.config;

import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import ch.admin.seco.jobroom.security.AuthoritiesConstants;

class AbstractSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;

    AbstractSecurityConfig(SecurityProblemSupport problemSupport) {
        this.problemSupport = problemSupport;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers("/app/**/*.{js,html}")
            .antMatchers("/i18n/**")
            .antMatchers("/content/**")
            .antMatchers("/test/**")
            .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
            .accessDeniedHandler(this.problemSupport);

        http.headers().frameOptions().sameOrigin();

        http.anonymous()
            .principal("anonymousUser")
            .authorities(AuthoritiesConstants.ANONYMOUS);

        http.authorizeRequests()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/authenticate").permitAll()
            .antMatchers("/api/active-system-notifications").permitAll()
            .antMatchers("/api/profile-info").permitAll()
            .antMatchers("/api/legal-terms/current").permitAll()
            .antMatchers("/api/messages/send-anonymous-message")
            /*-*/.hasAnyAuthority(
            /*----*/AuthoritiesConstants.ROLE_COMPANY,
            /*----*/AuthoritiesConstants.ROLE_PRIVATE_EMPLOYMENT_AGENT)
            .antMatchers("/api/**").authenticated()
            .antMatchers("/management/health").permitAll()
            .antMatchers("/management/info").permitAll()
            .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ROLE_ADMIN);
    }

}
