package pl.akmf.ksef.sdk.client.model.permission.search;

import java.time.OffsetDateTime;

public class EuEntityPermission {
    private String id;
    private String authorIdentifier;
    private EuEntityPermissionsAuthorIdentifierType authorIdentifierType;
    private String vatUeIdentifier;
    private String euEntityName;
    private String authorizedFingerprintIdentifier;
    private EuEntityPermissionsQueryPermissionType permissionScope;
    private String description;
    private OffsetDateTime startDate;

    public EuEntityPermission() {
    }

    public EuEntityPermission(String id, String authorIdentifier, EuEntityPermissionsAuthorIdentifierType authorIdentifierType, String vatUeIdentifier, String euEntityName, String authorizedFingerprintIdentifier, EuEntityPermissionsQueryPermissionType permissionScope, String description, OffsetDateTime startDate) {
        this.id = id;
        this.authorIdentifier = authorIdentifier;
        this.authorIdentifierType = authorIdentifierType;
        this.vatUeIdentifier = vatUeIdentifier;
        this.euEntityName = euEntityName;
        this.authorizedFingerprintIdentifier = authorizedFingerprintIdentifier;
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

    public String getAuthorIdentifier() {
        return authorIdentifier;
    }

    public void setAuthorIdentifier(String authorIdentifier) {
        this.authorIdentifier = authorIdentifier;
    }

    public EuEntityPermissionsAuthorIdentifierType getAuthorIdentifierType() {
        return authorIdentifierType;
    }

    public void setAuthorIdentifierType(EuEntityPermissionsAuthorIdentifierType authorIdentifierType) {
        this.authorIdentifierType = authorIdentifierType;
    }

    public String getVatUeIdentifier() {
        return vatUeIdentifier;
    }

    public void setVatUeIdentifier(String vatUeIdentifier) {
        this.vatUeIdentifier = vatUeIdentifier;
    }

    public String getEuEntityName() {
        return euEntityName;
    }

    public void setEuEntityName(String euEntityName) {
        this.euEntityName = euEntityName;
    }

    public String getAuthorizedFingerprintIdentifier() {
        return authorizedFingerprintIdentifier;
    }

    public void setAuthorizedFingerprintIdentifier(String authorizedFingerprintIdentifier) {
        this.authorizedFingerprintIdentifier = authorizedFingerprintIdentifier;
    }

    public EuEntityPermissionsQueryPermissionType getPermissionScope() {
        return permissionScope;
    }

    public void setPermissionScope(EuEntityPermissionsQueryPermissionType permissionScope) {
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

