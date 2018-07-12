package ch.admin.seco.jobroom.web.rest.errors;

import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;

import ch.admin.seco.jobroom.security.registration.AvgNotFoundException;
import ch.admin.seco.jobroom.security.registration.RegistrationException;
import ch.admin.seco.jobroom.security.registration.eiam.EiamClientRuntimeException;
import ch.admin.seco.jobroom.security.registration.uid.UidClientRuntimeException;
import ch.admin.seco.jobroom.security.registration.uid.UidCompanyNotFoundException;
import ch.admin.seco.jobroom.service.CouldNotLoadCurrentUserException;
import ch.admin.seco.jobroom.service.UserInfoNotFoundException;

/**
 * Controller advice to translate the server side exceptions thrown during the registration
 * process to client-friendly json structures. The error response follows
 * RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807)
 * Since it is a specific ExceptionHandler it is loaded first (Ordered. HIGHEST_PRECEDENCE)
 * in order to allow it to kick in before the specific ExceptionHandler if a match is found.
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionTranslatorForRegistration implements AdviceTrait {

    @ExceptionHandler(EiamClientRuntimeException.class)
    public ResponseEntity<Problem> handleEiamClientRuntimeException(EiamClientRuntimeException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.SERVICE_UNAVAILABLE)
            .with("message", ex.getMessage())
            .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler(UidClientRuntimeException.class)
    public ResponseEntity<Problem> handleUidClientRuntimeException(UidClientRuntimeException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.SERVICE_UNAVAILABLE)
            .with("message", ex.getMessage())
            .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler(UidCompanyNotFoundException.class)
    public ResponseEntity<Problem> handleCompanyNotFoundException(UidCompanyNotFoundException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.NOT_FOUND)
            .with("message", "The requested uid-company was not found.")
            .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler(AvgNotFoundException.class)
    public ResponseEntity<Problem> handleAvgNotFoundException(AvgNotFoundException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.NOT_FOUND)
            .with("message", "The requested avg-company was not found.")
            .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler(CouldNotLoadCurrentUserException.class)
    public ResponseEntity<Problem> handleCouldNotLoadCurrentUserException(CouldNotLoadCurrentUserException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.PRECONDITION_FAILED)
            .with("message", ex.getMessage())
            .build();
        return create(ex, problem, request);
    }

    @ExceptionHandler(UserInfoNotFoundException.class)
    public ResponseEntity<Problem> handleUserInfoNotFoundException(UserInfoNotFoundException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.NOT_FOUND)
            .with("message", ex.getMessage())
            .build();
        return create(ex, problem, request);
    }


    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Problem> handleRegistrationException(RegistrationException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.PRECONDITION_FAILED)
            .with("reason", ex.getClass().getSimpleName())
            .with("message", ex.getMessage())
            .build();
        return create(ex, problem, request);
    }

}
