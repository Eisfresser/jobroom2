package ch.admin.seco.jobroom.service;

import java.util.UUID;

public class BlacklistedAgentAlreadyExistsException extends Exception {

    private final UUID organizationId;

    BlacklistedAgentAlreadyExistsException(UUID organizationId) {
        super("There is already an existing blacklisted agent for organization: " + organizationId);
        this.organizationId = organizationId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }
}
