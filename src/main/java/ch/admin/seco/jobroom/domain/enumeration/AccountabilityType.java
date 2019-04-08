package ch.admin.seco.jobroom.domain.enumeration;

/**
 * The AccountabilityType enumeration.
 */
public enum AccountabilityType {
    USER(1); //, SUPERUSER(2);   // later we add other levels like super users

    private int code;

    AccountabilityType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

}
