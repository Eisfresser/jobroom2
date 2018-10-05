package ch.admin.seco.jobroom.domain.fixture;

import ch.admin.seco.jobroom.domain.Company;

public class CompanyFixture {

    public static Company testCompany() {
        Company company = new Company();
        company.setCity("Dübendorf");
        company.setStreet("Stadtstrasse 21");
        company.setZipCode("8600");
        company.setName("Stellenvermittlung24");
        return company;
    }
}
