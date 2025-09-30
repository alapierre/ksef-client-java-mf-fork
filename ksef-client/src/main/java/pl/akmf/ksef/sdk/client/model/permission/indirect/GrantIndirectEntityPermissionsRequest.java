package pl.akmf.ksef.sdk.client.model.permission.indirect;

import java.util.List;

public class GrantIndirectEntityPermissionsRequest {
    private SubjectIdentifier subjectIdentifier;
    private TargetIdentifier targetIdentifier;
    private List<IndirectPermissionType> permissions;
    private String description;

    public GrantIndirectEntityPermissionsRequest() {

    }

    public GrantIndirectEntityPermissionsRequest(SubjectIdentifier subjectIdentifier,
                                                 TargetIdentifier targetIdentifier,
                                                 List<IndirectPermissionType> permissions,
                                                 String description) {
        this.subjectIdentifier = subjectIdentifier;
        this.targetIdentifier = targetIdentifier;
        this.permissions = permissions;
        this.description = description;
    }

    public SubjectIdentifier getSubjectIdentifier() {
        return subjectIdentifier;
    }

    public void setSubjectIdentifier(SubjectIdentifier subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public List<IndirectPermissionType> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<IndirectPermissionType> permissions) {
        this.permissions = permissions;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TargetIdentifier getTargetIdentifier() {
        return targetIdentifier;
    }

    public void setTargetIdentifier(TargetIdentifier targetIdentifier) {
        this.targetIdentifier = targetIdentifier;
    }

}
