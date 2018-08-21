package ch.admin.seco.jobroom.service.dto;

import ch.admin.seco.jobroom.domain.enumeration.AccountabilityType;
import ch.admin.seco.jobroom.domain.enumeration.CompanySource;

public class AccountabilityDTO {

    private AccountabilityType type;

    private String companyId;

    private String companyName;

    private String companyExternalId;

    private CompanySource companySource;

    public AccountabilityDTO(AccountabilityType type, String companyId, String companyName, String companyExternalId, CompanySource companySource) {
        this.type = type;
        this.companyId = companyId;
        this.companyName = companyName;
        this.companyExternalId = companyExternalId;
        this.companySource = companySource;
    }

    public String getCompanyId() {
        return companyId;
    }

    public AccountabilityType getType() {
        return type;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyExternalId() {
        return companyExternalId;
    }

    public CompanySource getCompanySource() {
        return companySource;
    }
}
