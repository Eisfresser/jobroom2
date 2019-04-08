package ch.admin.seco.jobroom.domain;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Preconditions;

@Embeddable
@Access(AccessType.FIELD)
public final class CompanyContactTemplate {

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "company_id"))
    private CompanyId companyId;

    @NotNull
    @Size(max = 150)
    private String companyName;

    @NotNull
    @Size(max = 50)
    private String companyStreet;

    @Size(max = 10)
    private String companyHouseNr;

    @NotNull
    @Size(max = 4)
    private String companyZipCode;

    @NotNull
    @Size(max = 50)
    private String companyCity;

    private String phone;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Salutation salutation;

    private CompanyContactTemplate(Builder builder) {
        this.companyId = Preconditions.checkNotNull(builder.companyId);
        this.companyName = builder.companyName;
        this.companyStreet = builder.companyStreet;
        this.companyHouseNr = builder.companyHouseNr;
        this.companyZipCode = builder.companyZipCode;
        this.companyCity = builder.companyCity;
        this.phone = builder.phone;
        this.email = builder.email;
        this.salutation = builder.salutation;
    }

    private CompanyContactTemplate() {
        // FOR JPA
    }

    public CompanyId getCompanyId() {
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

    @Override
    public String toString() {
        return "CompanyContactTemplate{" +
            "companyId=" + companyId +
            ", companyName='" + companyName + '\'' +
            ", companyStreet='" + companyStreet + '\'' +
            ", companyHouseNr='" + companyHouseNr + '\'' +
            ", companyZipCode='" + companyZipCode + '\'' +
            ", companyCity='" + companyCity + '\'' +
            ", phone='" + phone + '\'' +
            ", email='" + email + '\'' +
            ", salutation=" + salutation +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyContactTemplate that = (CompanyContactTemplate) o;
        return Objects.equals(companyId, that.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CompanyId companyId;
        private String companyName;
        private String companyStreet;
        private String companyHouseNr;
        private String companyZipCode;
        private String companyCity;
        private String phone;
        private String email;
        private Salutation salutation;

        public Builder setCompanyId(CompanyId companyId) {
            this.companyId = companyId;
            return this;
        }

        public Builder setCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder setCompanyStreet(String companyStreet) {
            this.companyStreet = companyStreet;
            return this;
        }

        public Builder setCompanyHouseNr(String companyHouseNr) {
            this.companyHouseNr = companyHouseNr;
            return this;
        }

        public Builder setCompanyZipCode(String companyZipCode) {
            this.companyZipCode = companyZipCode;
            return this;
        }

        public Builder setCompanyCity(String companyCity) {
            this.companyCity = companyCity;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setSalutation(Salutation salutation) {
            this.salutation = salutation;
            return this;
        }

        public CompanyContactTemplate build() {
            return new CompanyContactTemplate(this);
        }

        public Builder from(Company company) {
            return this.setCompanyId(company.getId())
                .setCompanyName(company.getName())
                .setCompanyStreet(company.getStreet())
                .setCompanyZipCode(company.getZipCode())
                .setCompanyCity(company.getCity());
        }
    }
}
