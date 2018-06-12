package ch.admin.seco.jobroom.config;

/**
 * Application constants.
 */
public final class Constants {

    //Regex for acceptable logins
    public static final String LOGIN_REGEX = "^[_'.@A-Za-z0-9éèäöü&-]*$";

    public static final String SYSTEM_ACCOUNT = "system";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String DEFAULT_LANGUAGE = "de";

    public static final String TYPE_EMPLOYER = "EMPLOYER";
    public static final String TYPE_AGENT = "AGENT";
    public static final String TYPE_UNKOWN = "UNKNOWN";
    public static final String USER_TYPE_REGEX = "^(EMPLOYER|AGENT|UNKNOWN)$";

    private Constants() {
    }
}
