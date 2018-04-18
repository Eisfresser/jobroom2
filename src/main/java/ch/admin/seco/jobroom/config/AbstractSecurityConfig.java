package ch.admin.seco.jobroom.config;

import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.CacheControlHeadersWriter;
import org.springframework.security.web.header.writers.DelegatingRequestMatcherHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AbstractSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;

    public AbstractSecurityConfig(SecurityProblemSupport problemSupport) {
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

        http.csrf().disable()
        .exceptionHandling()
            .accessDeniedHandler(problemSupport)
            //TODO: is this really needed?
            .authenticationEntryPoint(problemSupport)
            //.and().authorizeRequests().antMatchers("/**").fullyAuthenticated()
        .and()
            .headers()
                .cacheControl().disable()
                .addHeaderWriter(notResourcesHeaderWriter)
                .frameOptions().disable();
    }


    /*protected AccessDeniedHandler forbiddenDeniedHandler() {
        return new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                httpServletResponse.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied");
            }
        };
    }*/
}
