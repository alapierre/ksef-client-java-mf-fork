package pl.akmf.ksef.sdk.client.model.permission.search;

import java.time.OffsetDateTime;

public class EntityAuthorizationGrant {
    private String id;
    private String authorIdentifier;
    private EntityAuthorizationsAuthorIdentifierType authorIdentifierType;
    private String authorizedEntityIdentifier;
    private EntityAuthorizationsAuthorizedEntityIdentifierType authorizedEntityIdentifierType;
    private String authorizingEntityIdentifier;
    private EntityAuthorizationsAuthorizingEntityIdentifierType authorizingEntityIdentifierType;
    private InvoicePermissionType authorizationScope;
    private String description;
    private OffsetDateTime startDate;

    public EntityAuthorizationGrant(String id, String authorIdentifier, EntityAuthorizationsAuthorIdentifierType authorIdentifierType, String authorizedEntityIdentifier, EntityAuthorizationsAuthorizedEntityIdentifierType authorizedEntityIdentifierType, String authorizingEntityIdentifier, EntityAuthorizationsAuthorizingEntityIdentifierType authorizingEntityIdentifierType, InvoicePermissionType authorizationScope, String description, OffsetDateTime startDate) {
        this.id = id;
        this.authorIdentifier = authorIdentifier;
        this.authorIdentifierType = authorIdentifierType;
        this.authorizedEntityIdentifier = authorizedEntityIdentifier;
        this.authorizedEntityIdentifierType = authorizedEntityIdentifierType;
        this.authorizingEntityIdentifier = authorizingEntityIdentifier;
        this.authorizingEntityIdentifierType = authorizingEntityIdentifierType;
        this.authorizationScope = authorizationScope;
        this.description = description;
        this.startDate = startDate;
    }

    public EntityAuthorizationGrant() {
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

    public EntityAuthorizationsAuthorIdentifierType getAuthorIdentifierType() {
        return authorIdentifierType;
    }

    public void setAuthorIdentifierType(EntityAuthorizationsAuthorIdentifierType authorIdentifierType) {
        this.authorIdentifierType = authorIdentifierType;
    }

    public String getAuthorizedEntityIdentifier() {
        return authorizedEntityIdentifier;
    }

    public void setAuthorizedEntityIdentifier(String authorizedEntityIdentifier) {
        this.authorizedEntityIdentifier = authorizedEntityIdentifier;
    }

    public EntityAuthorizationsAuthorizedEntityIdentifierType getAuthorizedEntityIdentifierType() {
        return authorizedEntityIdentifierType;
    }

    public void setAuthorizedEntityIdentifierType(EntityAuthorizationsAuthorizedEntityIdentifierType authorizedEntityIdentifierType) {
        this.authorizedEntityIdentifierType = authorizedEntityIdentifierType;
    }

    public String getAuthorizingEntityIdentifier() {
        return authorizingEntityIdentifier;
    }

    public void setAuthorizingEntityIdentifier(String authorizingEntityIdentifier) {
        this.authorizingEntityIdentifier = authorizingEntityIdentifier;
    }

    public EntityAuthorizationsAuthorizingEntityIdentifierType getAuthorizingEntityIdentifierType() {
        return authorizingEntityIdentifierType;
    }

    public void setAuthorizingEntityIdentifierType(EntityAuthorizationsAuthorizingEntityIdentifierType authorizingEntityIdentifierType) {
        this.authorizingEntityIdentifierType = authorizingEntityIdentifierType;
    }

    public InvoicePermissionType getAuthorizationScope() {
        return authorizationScope;
    }

    public void setAuthorizationScope(InvoicePermissionType authorizationScope) {
        this.authorizationScope = authorizationScope;
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
