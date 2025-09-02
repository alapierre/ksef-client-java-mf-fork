package pl.akmf.ksef.sdk.client.model.permission.search;

import java.time.OffsetDateTime;

public class SubunitPermission {
    private String id;
    private String authorizedIdentifier;
    private SubunitPermissionsSubjectIdentifierType authorizedIdentifierType;
    private String subunitIdentifier;
    private SubunitPermissionsSubunitIdentifierType subunitIdentifierType;
    private String authorIdentifier;
    private SubunitPermissionsAuthorIdentifierType authorIdentifierType;
    private SubunitPermissionScope permissionScope;
    private String description;
    private OffsetDateTime startDate;

    SubunitPermission() {
    }

    public SubunitPermission(String authorizedIdentifier, SubunitPermissionsSubjectIdentifierType authorizedIdentifierType, String subunitIdentifier, SubunitPermissionsSubunitIdentifierType subunitIdentifierType, String authorIdentifier, SubunitPermissionsAuthorIdentifierType authorIdentifierType, SubunitPermissionScope permissionScope, String description, OffsetDateTime startDate) {
        this.authorizedIdentifier = authorizedIdentifier;
        this.authorizedIdentifierType = authorizedIdentifierType;
        this.subunitIdentifier = subunitIdentifier;
        this.subunitIdentifierType = subunitIdentifierType;
        this.authorIdentifier = authorIdentifier;
        this.authorIdentifierType = authorIdentifierType;
        this.permissionScope = permissionScope;
        this.description = description;
        this.startDate = startDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorizedIdentifier() {
        return authorizedIdentifier;
    }

    public void setAuthorizedIdentifier(String authorizedIdentifier) {
        this.authorizedIdentifier = authorizedIdentifier;
    }

    public SubunitPermissionsSubjectIdentifierType getAuthorizedIdentifierType() {
        return authorizedIdentifierType;
    }

    public void setAuthorizedIdentifierType(SubunitPermissionsSubjectIdentifierType authorizedIdentifierType) {
        this.authorizedIdentifierType = authorizedIdentifierType;
    }

    public String getSubunitIdentifier() {
        return subunitIdentifier;
    }

    public void setSubunitIdentifier(String subunitIdentifier) {
        this.subunitIdentifier = subunitIdentifier;
    }

    public SubunitPermissionsSubunitIdentifierType getSubunitIdentifierType() {
        return subunitIdentifierType;
    }

    public void setSubunitIdentifierType(SubunitPermissionsSubunitIdentifierType subunitIdentifierType) {
        this.subunitIdentifierType = subunitIdentifierType;
    }

    public String getAuthorIdentifier() {
        return authorIdentifier;
    }

    public void setAuthorIdentifier(String authorIdentifier) {
        this.authorIdentifier = authorIdentifier;
    }

    public SubunitPermissionsAuthorIdentifierType getAuthorIdentifierType() {
        return authorIdentifierType;
    }

    public void setAuthorIdentifierType(SubunitPermissionsAuthorIdentifierType authorIdentifierType) {
        this.authorIdentifierType = authorIdentifierType;
    }

    public SubunitPermissionScope getPermissionScope() {
        return permissionScope;
    }

    public void setPermissionScope(SubunitPermissionScope permissionScope) {
        this.permissionScope = permissionScope;
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

