package pl.akmf.ksef.sdk.client.model.permission.search;

import java.time.OffsetDateTime;

public class SubordinateEntityRole {
    private String subordinateEntityIdentifier;
    private SubordinateRoleSubordinateEntityIdentifierType subordinateEntityIdentifierType;
    private SubordinateEntityRoleType role;
    private String description;
    private OffsetDateTime startDate;

    public SubordinateEntityRole() {
    }

    public SubordinateEntityRole(String subordinateEntityIdentifier, SubordinateRoleSubordinateEntityIdentifierType subordinateEntityIdentifierType, SubordinateEntityRoleType role, String description, OffsetDateTime startDate) {
        this.subordinateEntityIdentifier = subordinateEntityIdentifier;
        this.subordinateEntityIdentifierType = subordinateEntityIdentifierType;
        this.role = role;
        this.description = description;
        this.startDate = startDate;
    }

    public String getSubordinateEntityIdentifier() {
        return subordinateEntityIdentifier;
    }

    public void setSubordinateEntityIdentifier(String subordinateEntityIdentifier) {
        this.subordinateEntityIdentifier = subordinateEntityIdentifier;
    }

    public SubordinateRoleSubordinateEntityIdentifierType getSubordinateEntityIdentifierType() {
        return subordinateEntityIdentifierType;
    }

    public void setSubordinateEntityIdentifierType(SubordinateRoleSubordinateEntityIdentifierType subordinateEntityIdentifierType) {
        this.subordinateEntityIdentifierType = subordinateEntityIdentifierType;
    }

    public SubordinateEntityRoleType getRole() {
        return role;
    }

    public void setRole(SubordinateEntityRoleType role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }
}

