package ch.admin.seco.jobroom.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;

import org.springframework.util.Assert;

@Embeddable
@Access(AccessType.FIELD)
public class CompanyId implements Serializable {

    private final String value;

    public CompanyId() {
        this(IdGenerator.timeBasedUUID().toString());
    }

    public CompanyId(String value) {
        Assert.notNull(value, "Provide a value; null-value ids are not allowed.");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean sameValueObjectAs(CompanyId other) {
        return (other != null) && this.value.equals(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CompanyId that = (CompanyId) o;
        return sameValueObjectAs(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CompanyId{" +
            "value='" + value + '\'' +
            '}';
    }

}
