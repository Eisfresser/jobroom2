package ch.admin.seco.jobroom.service;

public class BlacklistedAgentAlreadyExistsException extends Exception {

    private final String organizationId;

    BlacklistedAgentAlreadyExistsException(String organizationId) {
        super("There is already an existing blacklisted agent for organization: " + organizationId);
        this.organizationId = organizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }
}
