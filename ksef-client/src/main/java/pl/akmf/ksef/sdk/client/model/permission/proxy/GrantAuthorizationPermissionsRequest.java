package pl.akmf.ksef.sdk.client.model.permission.proxy;

public class GrantAuthorizationPermissionsRequest {
    private SubjectIdentifier  subjectIdentifier;
    private ProxyEntityPermissionType permission;
    private String description;

    public GrantAuthorizationPermissionsRequest() {

    }

    public GrantAuthorizationPermissionsRequest(SubjectIdentifier subjectIdentifier, ProxyEntityPermissionType permission, String description) {
        this.subjectIdentifier = subjectIdentifier;
        this.permission = permission;
        this.description = description;
    }

    public SubjectIdentifier  getSubjectIdentifier() {
        return subjectIdentifier;
    }

    public void setSubjectIdentifier(SubjectIdentifier  subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public ProxyEntityPermissionType getPermission() {
        return permission;
    }

    public void setPermission(ProxyEntityPermissionType permission) {
        this.permission = permission;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
