package ch.admin.seco.jobroom.service.dto;

import javax.validation.constraints.NotBlank;

import ch.admin.seco.jobroom.domain.Salutation;

public class CompanyContactTemplateDTO {

    @NotBlank
    private String companyId;

    private String companyName;

    private String companyStreet;

    private String companyHouseNr;

    private String companyZipCode;

    private String companyCity;

    private String phone;

    private String email;

    private Salutation salutation;

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompanyStreet(String companyStreet) {
        this.companyStreet = companyStreet;
    }

    public void setCompanyHouseNr(String companyHouseNr) {
        this.companyHouseNr = companyHouseNr;
    }

    public void setCompanyZipCode(String companyZipCode) {
        this.companyZipCode = companyZipCode;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSalutation(Salutation salutation) {
        this.salutation = salutation;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyStreet() {
        return companyStreet;
    }

    public String getCompanyHouseNr() {
        return companyHouseNr;
    }

    public String getCompanyZipCode() {
        return companyZipCode;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Salutation getSalutation() {
        return salutation;
    }
}
