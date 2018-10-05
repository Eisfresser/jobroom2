package ch.admin.seco.jobroom.domain.fixture;

import ch.admin.seco.jobroom.domain.Organization;

public class OrganizationFixture {

    public static Organization testOrganizationEmpty() {
        return new Organization();
    }

    public static Organization testOrganization() {
        return testOrganizationEmpty()
            .externalId("externalId")
            .name("name")
            .street("street")
            .zipCode("8001")
            .city("city")
            .email("anonymous@mail.ch")
            .phone("076 12 345 6789")
            .active(false);
    }
}

