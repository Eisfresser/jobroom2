package ch.admin.seco.jobroom.security.saml.utils;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HttpStatusEntryPoint implements AuthenticationEntryPoint {

    private final HttpStatus httpStatus;

    public HttpStatusEntryPoint(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.resetBuffer();
        response.setStatus(httpStatus.value());
    }
}
