package ch.admin.seco.jobroom.security.registration.eiam;

public enum EiamClientRole {

    ROLE_JOBSEEKER("ROLE_JOBSEEKER"),
    ROLE_COMPANY("ROLE_COMPANY"),
    ROLE_PRIVATE_EMPLOYMENT_AGENT("ROLE_PRIVATE_EMPLOYMENT_AGENT");

    private final String name;

    EiamClientRole(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
