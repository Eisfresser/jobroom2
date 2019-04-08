package ch.admin.seco.jobroom.domain;

import static ch.admin.seco.jobroom.domain.IdGenerator.timeBasedUUID;
import static javax.persistence.AccessType.FIELD;
import static org.springframework.util.Assert.notNull;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.Embeddable;

@Embeddable
@Access(FIELD)
public class BlacklistedAgentId implements Serializable {

    private final String value;

    public BlacklistedAgentId() {
        this(timeBasedUUID().toString());
    }

    public BlacklistedAgentId(String value) {
        notNull(value, "Provide a value; null-value ids are not allowed.");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlacklistedAgentId that = (BlacklistedAgentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "BlacklistedAgentId{" +
            "value='" + value + '\'' +
            '}';
    }
}
