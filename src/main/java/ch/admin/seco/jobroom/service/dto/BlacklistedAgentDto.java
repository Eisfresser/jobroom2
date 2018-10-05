package ch.admin.seco.jobroom.service.dto;

import java.time.LocalDateTime;

import ch.admin.seco.jobroom.domain.BlacklistedAgentStatus;

public class BlacklistedAgentDto {

    private String id;

    private String externalId;

    private String createdBy;

    private BlacklistedAgentStatus status;

    private String name;

    private String street;

    private String zipCode;

    private String city;

    private LocalDateTime blacklistedAt;

    private int blacklistingCounter;

    public String getId() {
        return id;
    }

    public BlacklistedAgentDto setId(String id) {
        this.id = id;
        return this;
    }

    public String getExternalId() {
        return externalId;
    }

    public BlacklistedAgentDto setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public BlacklistedAgentDto setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public BlacklistedAgentStatus getStatus() {
        return status;
    }

    public BlacklistedAgentDto setStatus(BlacklistedAgentStatus status) {
        this.status = status;
        return this;
    }

    public String getName() {
        return name;
    }

    public BlacklistedAgentDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getStreet() {
        return street;
    }

    public BlacklistedAgentDto setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getZipCode() {
        return zipCode;
    }

    public BlacklistedAgentDto setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getCity() {
        return city;
    }

    public BlacklistedAgentDto setCity(String city) {
        this.city = city;
        return this;
    }

    public LocalDateTime getBlacklistedAt() {
        return blacklistedAt;
    }

    public BlacklistedAgentDto setBlacklistedAt(LocalDateTime blacklistedAt) {
        this.blacklistedAt = blacklistedAt;
        return this;
    }

    public int getBlacklistingCounter() {
        return blacklistingCounter;
    }

    public BlacklistedAgentDto setBlacklistingCounter(int blacklistingCounter) {
        this.blacklistingCounter = blacklistingCounter;
        return this;
    }
}
