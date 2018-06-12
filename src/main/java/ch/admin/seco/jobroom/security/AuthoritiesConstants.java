package ch.admin.seco.jobroom.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    // every user authenticated via eIAM needs to have this role to be allowed to use Jobroom
    public static final String ROLE_ALLOW = "ROLE_ALLOW";

    // user which started the registration process, but does not have a real application role yet
    public static final String ROLE_REGISTRATION = "ROLE_REGISTRATION";

    // the 3 main application roles
    public static final String ROLE_JOBSEEKER_CLIENT = "ROLE_JOBSEEKER_CLIENT";
    public static final String ROLE_COMPANY = "ROLE_COMPANY";
    public static final String ROLE_PRIVATE_EMPLOYMENT_AGENT = "ROLE_PRIVATE_EMPLOYMENT_AGENT";

    // special roles (admin etc.)
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SYSADMIN = "ROLE_SYSADMIN";

    // roles only used by the old login mechanism
    public static final String USER = "ROLE_USER";
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    // currently not used
    public static final String ROLE_PUBLIC_EMPLOYMENT_SERVICE = "ROLE_PUBLIC_EMPLOYMENT_SERVICE";

    private AuthoritiesConstants() {
    }
}
