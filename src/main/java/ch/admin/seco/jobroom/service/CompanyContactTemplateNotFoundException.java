package ch.admin.seco.jobroom.service;

import ch.admin.seco.jobroom.domain.CompanyId;

public class CompanyContactTemplateNotFoundException extends Exception {
    public CompanyContactTemplateNotFoundException(CompanyId companyId) {
        super("No Template found for companyId " + companyId.getValue());
    }
}
