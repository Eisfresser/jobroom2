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

import ch.admin.seco.jobroom.security.registration.NoValidPrincipalException;
import ch.admin.seco.jobroom.security.registration.StesServiceException;
import ch.admin.seco.jobroom.security.registration.UserAlreadyExistsException;
import ch.admin.seco.jobroom.security.registration.eiam.RoleCouldNotBeAddedException;
import ch.admin.seco.jobroom.security.registration.uid.CompanyNotFoundException;
import ch.admin.seco.jobroom.security.registration.uid.UidClientException;
import ch.admin.seco.jobroom.security.registration.uid.UidNotUniqueException;

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

    /**
     * The user session did not contain a valid principal. This could probably be the
     * cause of a not proper authenticated user. The client should ask the user to
     * close the browser, re-enter the base URL of jobroom and authenticate through the
     * eIAM. Should the problem remain, then the user should contact the service desk.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(NoValidPrincipalException.class)
    public ResponseEntity<Problem> handleNoValidPrincipalException(NoValidPrincipalException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.FORBIDDEN)
            .with("message", "The user is authenticated, but there is no valid principal in the session.")
            .build();
        return create(ex, problem, request);
    }

    /**
     * The user was alread registered in Jobroom. This could for example happen, if the
     * user has manually entered a registering URL. The client should ask the user to
     * close the browser, re-enter the base URL of jobroom and authenticate through the
     * eIAM. Should the problem remain, then the user should contact the service desk.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Problem> handleUserAlreadyExistsException(UserAlreadyExistsException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.BAD_REQUEST)
            .with("message", "The user trying to register has already been registered.")
            .build();
        return create(ex, problem, request);
    }

    /**
     * This seems to be a technical problem with the eIAM webservice. The client should
     * ask the user to contact the service desk, so that the problem can be narrowed.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(RoleCouldNotBeAddedException.class)
    public ResponseEntity<Problem> handleRoleCouldNotBeAddedException(RoleCouldNotBeAddedException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .with("message", "The new role could not be added to the user.")
            .build();
        return create(ex, problem, request);
    }

    /**
     * The UID webservice did not handle the request properly. This could be a temporary
     * problem. The client should ask the user to try later again, since this could be
     * because of the UID webservice not to be available. If the problem remains, the user
     * should contact the service desk.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(UidClientException.class)
    public ResponseEntity<Problem> handleUidClientException(UidClientException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.SERVICE_UNAVAILABLE)
            .with("message", ex.getMessage())
            .build();
        return create(ex, problem, request);
    }

    /**
     * In the case of this exception the user has either entered a wrong UID or his/her
     * company is probably not yet registered in the UID register (or it is not yet in a
     * status that makes it visible to the public). The client should ask the user to
     * either wait or to contact the service desk so that the problem can be narrowed.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<Problem> handleCompanyNotFoundException(CompanyNotFoundException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.BAD_REQUEST)
            .with("message", "The requested company was not found.")
            .build();
        return create(ex, problem, request);
    }

    /**
     * In the case of this exception the user has either entered a wrong UID or there is a
     * data problem in the UID register. The client should ask the user to re-select the
     * company and if the problem remains, to contact the service desk and report, that
     * his provided UID causes multiple firms to be found.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(UidNotUniqueException.class)
    public ResponseEntity<Problem> handleUidNotUniqueException(UidNotUniqueException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .with("message", "The requested company was not found because multiple entries correspond with the given id.")
            .build();
        return create(ex, problem, request);
    }

    /**
     * In the case of this exception the entered validation data could not be cheched
     * because the candidate service was not available or not able to handle the request.
     * The client should ask the user to try again later.
     * If the problem remains the user should contact the service desk.
     * @param ex the exception to be handled by this method
     * @param request   the current web request
     * @return client-friendly json error response
     */
    @ExceptionHandler(StesServiceException.class)
    public ResponseEntity<Problem> handleNoSuchStesUserException(StesServiceException ex, NativeWebRequest request) {
        Problem problem = Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .with("message", "There was a problem during the verification of the entered data.")
            .build();
        return create(ex, problem, request);
    }

}
