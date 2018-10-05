package ch.admin.seco.jobroom.service;

import java.util.UUID;

public class OrganizationNotFoundException extends Exception {

    private final UUID organizationId;

    OrganizationNotFoundException(UUID organizationId) {
        super("No Organization found having Id:" + organizationId);
        this.organizationId = organizationId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }
}
