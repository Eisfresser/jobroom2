package ch.admin.seco.jobroom.service;

public class OrganizationNotFoundException extends Exception {

    private final String organizationId;

    OrganizationNotFoundException(String organizationId) {
        super("No Organization found having Id:" + organizationId);
        this.organizationId = organizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
