package pl.akmf.ksef.sdk.client.model.permission.search;

import java.time.OffsetDateTime;

public class PersonPermission {
    private String id;
    private String authorizedIdentifier;
    private PersonPermissionsAuthorizedIdentifierType authorizedIdentifierType;
    private String targetIdentifier;
    private PersonPermissionsTargetIdentifierType targetIdentifierType;
    private String authorIdentifier;
    private PersonPermissionsAuthorIdentifierType authorIdentifierType;
    private PersonPermissionScope permissionScope;
    private String description;
    private PermissionState permissionState;
    private OffsetDateTime startDate;
    private boolean canDelegate;

    public PersonPermission() {
    }

    public PersonPermission(String id, String authorizedIdentifier, PersonPermissionsAuthorizedIdentifierType authorizedIdentifierType, String targetIdentifier, PersonPermissionsTargetIdentifierType targetIdentifierType, String authorIdentifier, PersonPermissionsAuthorIdentifierType authorIdentifierType, PersonPermissionScope permissionScope, String description, PermissionState permissionState, OffsetDateTime startDate) {
        this.id = id;
        this.authorizedIdentifier = authorizedIdentifier;
        this.authorizedIdentifierType = authorizedIdentifierType;
        this.targetIdentifier = targetIdentifier;
        this.targetIdentifierType = targetIdentifierType;
        this.authorIdentifier = authorIdentifier;
        this.authorIdentifierType = authorIdentifierType;
        this.permissionScope = permissionScope;
        this.description = description;
        this.permissionState = permissionState;
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

    public PersonPermissionsAuthorizedIdentifierType getAuthorizedIdentifierType() {
        return authorizedIdentifierType;
    }

    public void setAuthorizedIdentifierType(PersonPermissionsAuthorizedIdentifierType authorizedIdentifierType) {
        this.authorizedIdentifierType = authorizedIdentifierType;
    }

    public String getTargetIdentifier() {
        return targetIdentifier;
    }

    public void setTargetIdentifier(String targetIdentifier) {
        this.targetIdentifier = targetIdentifier;
    }

    public PersonPermissionsTargetIdentifierType getTargetIdentifierType() {
        return targetIdentifierType;
    }

    public void setTargetIdentifierType(PersonPermissionsTargetIdentifierType targetIdentifierType) {
        this.targetIdentifierType = targetIdentifierType;
    }

    public String getAuthorIdentifier() {
        return authorIdentifier;
    }

    public void setAuthorIdentifier(String authorIdentifier) {
        this.authorIdentifier = authorIdentifier;
    }

    public PersonPermissionsAuthorIdentifierType getAuthorIdentifierType() {
        return authorIdentifierType;
    }

    public void setAuthorIdentifierType(PersonPermissionsAuthorIdentifierType authorIdentifierType) {
        this.authorIdentifierType = authorIdentifierType;
    }

    public PersonPermissionScope getPermissionScope() {
        return permissionScope;
    }

    public void setPermissionScope(PersonPermissionScope permissionScope) {
        this.permissionScope = permissionScope;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PermissionState getPermissionState() {
        return permissionState;
    }

    public void setPermissionState(PermissionState permissionState) {
        this.permissionState = permissionState;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public boolean isCanDelegate() {
        return canDelegate;
    }

    public void setCanDelegate(boolean canDelegate) {
        this.canDelegate = canDelegate;
    }
}

