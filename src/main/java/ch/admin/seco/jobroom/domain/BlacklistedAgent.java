package ch.admin.seco.jobroom.domain;

import static ch.admin.seco.jobroom.domain.BlacklistedAgentStatus.ACTIVE;
import static java.time.LocalDateTime.now;
import static javax.persistence.EnumType.STRING;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Preconditions;

import ch.admin.seco.jobroom.security.UserPrincipal;

@Entity
public final class BlacklistedAgent implements Serializable {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id"))
    @Valid
    private BlacklistedAgentId id;

    @NotNull
    @Size(max = 12)
    private String externalId;

    @Enumerated(STRING)
    @NotNull
    private BlacklistedAgentStatus status;

    @NotNull
    @Size(max = 150)
    private String name;

    @Size(max = 50)
    private String street;

    @Size(max = 4)
    private String zipCode;

    @Size(max = 50)
    private String city;

    @NotNull
    @Size(max = 50)
    private String createdBy;

    @NotNull
    private LocalDateTime blacklistedAt;

    private int blacklistingCounter;

    private BlacklistedAgent(Builder builder) {
        this.id = Preconditions.checkNotNull(builder.id);
        this.createdBy = builder.createdBy;
        this.blacklistedAt = now();
        this.status = ACTIVE;
        this.externalId = builder.externalId;
        this.name = builder.name;
        this.street = builder.street;
        this.zipCode = builder.zipCode;
        this.city = builder.city;
    }

    private BlacklistedAgent() {
        // FOR JPA
    }

    public String getCity() {
        return city;
    }

    public BlacklistedAgentId getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getBlacklistedAt() {
        return blacklistedAt;
    }

    public BlacklistedAgentStatus getStatus() {
        return status;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlacklistedAgent that = (BlacklistedAgent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, externalId, createdBy, status, name, street, zipCode, city, blacklistedAt);
    }

    @Override
    public String toString() {
        return "BlacklistedAgent{" +
            "id=" + id +
            ", externalId='" + externalId + '\'' +
            ", createdBy='" + createdBy + '\'' +
            ", status=" + status +
            ", name='" + name + '\'' +
            ", street='" + street + '\'' +
            ", zipCode='" + zipCode + '\'' +
            ", city='" + city + '\'' +
            ", blacklistedAt=" + blacklistedAt +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public void changeStatus(BlacklistedAgentStatus status, UserPrincipal principal) {
        this.createdBy = principal.getUsername();
        this.status = status;
        if (this.status == ACTIVE) {
            blacklistingCounter++;
        }
    }

    public int getBlacklistingCounter() {
        return blacklistingCounter;
    }

    public static class Builder {

        private BlacklistedAgentId id;

        private String createdBy;

        public String name;

        public String street;

        public String zipCode;

        public String city;

        public String externalId;

        public Builder setId(BlacklistedAgentId id) {
            this.id = id;
            return this;
        }

        public Builder setCreatedBy(UserPrincipal userPrincipal) {
            this.createdBy = userPrincipal.getUsername();
            return this;
        }

        public Builder setExternalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder setZipCode(String zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public BlacklistedAgent build() {
            return new BlacklistedAgent(this);
        }
    }
}
