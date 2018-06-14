package ch.admin.seco.jobroom.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

import ch.admin.seco.jobroom.domain.enumeration.AccountabilityType;

/**
 * A company referenced by users. We have two types of companies either from UID register
 * (for employers) or from the AVG list (for job agencies). Note that the UID register
 * cannot provide some fields (email, phone).
 */

@Embeddable
@Access(AccessType.FIELD)
public class Accountability implements Serializable {

    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "accountability_type")
    private AccountabilityType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", referencedColumnName = "id")
    private Company company;

    public Accountability(AccountabilityType type, Company company) {
        Assert.notNull(company, "A company must be set.");
        Assert.notNull(type, "A type must be set.");
        this.type = type;
        this.company = company;
    }

    protected Accountability() {
        // FOR JPA
    }

    public AccountabilityType getType() {
        return type;
    }

    public Company getCompany() {
        return company;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, company);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Accountability)) {
            return false;
        }
        Accountability that = (Accountability) o;
        return type == that.type &&
            Objects.equals(company, that.company);
    }

    @Override
    public String toString() {
        return "Accountability{" +
            "type=" + type +
            ", company=" + company +
            '}';
    }
}
