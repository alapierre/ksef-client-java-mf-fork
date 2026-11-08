package pl.akmf.ksef.sdk.client.model.limit;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeSubjectCertificateLimitRequest {
    private EnrollmentLimit enrollment;
    private CertificateLimit certificate;
    private SubjectType subjectIdentifierType;

    public ChangeSubjectCertificateLimitRequest() {
    }

    public ChangeSubjectCertificateLimitRequest(EnrollmentLimit enrollment, CertificateLimit certificate, SubjectType subjectIdentifierType) {
        this.enrollment = enrollment;
        this.certificate = certificate;
        this.subjectIdentifierType = subjectIdentifierType;
    }

    public enum SubjectType {
        NIP("Nip"),
        PESEL("Pesel"),
        FINGERPRING("Fingerprint");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        SubjectType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SubjectType fromValue(String value) {
            for (SubjectType b : SubjectType.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
