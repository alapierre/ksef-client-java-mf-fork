package pl.akmf.ksef.sdk.api.builders.permission.indirect;

import pl.akmf.ksef.sdk.client.model.permission.indirect.IndirectPermissionsGrantRequest;
import pl.akmf.ksef.sdk.client.model.permission.indirect.IndirectPermissionType;
import pl.akmf.ksef.sdk.client.model.permission.indirect.SubjectIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.indirect.TargetIdentifier;

import java.util.List;

public class GrantIndirectEntityPermissionsRequestBuilder {
    private SubjectIdentifier subjectIdentifier;
    private TargetIdentifier targetIdentifier;
    private List<IndirectPermissionType> permissions;
    private String description;

    public GrantIndirectEntityPermissionsRequestBuilder withSubjectIdentifier(SubjectIdentifier subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
        return this;
    }

    public GrantIndirectEntityPermissionsRequestBuilder withPermissions(List<IndirectPermissionType> permissions) {
        this.permissions = permissions;
        return this;
    }

    public GrantIndirectEntityPermissionsRequestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public GrantIndirectEntityPermissionsRequestBuilder withTargetIdentifier(TargetIdentifier targetIdentifier) {
        this.targetIdentifier = targetIdentifier;
        return this;
    }

    public IndirectPermissionsGrantRequest build() {
        return new IndirectPermissionsGrantRequest(subjectIdentifier, targetIdentifier, permissions, description);
    }
}
