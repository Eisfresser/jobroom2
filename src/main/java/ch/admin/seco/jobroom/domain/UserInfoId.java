package ch.admin.seco.jobroom.domain;

import org.springframework.util.Assert;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Access(AccessType.FIELD)
public class UserInfoId implements Serializable {

    private final String value;

    public UserInfoId() {
        this(IdGenerator.timeBasedUUID().toString());
    }

    public UserInfoId(String value) {
        Assert.notNull(value, "Provide a value; null-value ids are not allowed.");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoId that = (UserInfoId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserInfoId{" +
            "value='" + value + '\'' +
            '}';
    }
}
