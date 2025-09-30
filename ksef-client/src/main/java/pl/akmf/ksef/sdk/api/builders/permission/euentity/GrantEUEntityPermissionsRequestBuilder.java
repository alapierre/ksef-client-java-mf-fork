package pl.akmf.ksef.sdk.api.builders.permission.euentity;

import pl.akmf.ksef.sdk.client.model.permission.euentity.ContextIdentifier;
import pl.akmf.ksef.sdk.client.model.permission.euentity.EuEntityPermissionsGrantRequest;
import pl.akmf.ksef.sdk.client.model.permission.euentity.SubjectIdentifier;

public class GrantEUEntityPermissionsRequestBuilder {
    private SubjectIdentifier subjectIdentifier;
    private ContextIdentifier contextIdentifier;
    private String description;
    private String subjectName;

    public GrantEUEntityPermissionsRequestBuilder withSubject(SubjectIdentifier subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
        return this;
    }

    public GrantEUEntityPermissionsRequestBuilder withContext(ContextIdentifier subjectIdentifier) {
        this.contextIdentifier = subjectIdentifier;
        return this;
    }

    public GrantEUEntityPermissionsRequestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    public GrantEUEntityPermissionsRequestBuilder withSubjectName(String subjectName) {
        this.subjectName = subjectName;
        return this;
    }

    public EuEntityPermissionsGrantRequest build() {
        EuEntityPermissionsGrantRequest request = new EuEntityPermissionsGrantRequest();
        request.setSubjectIdentifier(subjectIdentifier);
        request.setContextIdentifier(contextIdentifier);
        request.setDescription(description);
        request.setSubjectName(subjectName);
        return request;
    }
}
