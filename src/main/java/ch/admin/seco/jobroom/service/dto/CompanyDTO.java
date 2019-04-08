package ch.admin.seco.jobroom.service.dto;

import ch.admin.seco.jobroom.domain.enumeration.CompanySource;

public class CompanyDTO {

    private String id;

    private String externalId;

    private String name;

    private String street;

    private String zipCode;

    private String city;

    private String email;

    private String phone;

    private CompanySource source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CompanySource getSource() {
        return source;
    }

    public void setSource(CompanySource source) {
        this.source = source;
    }

}
