package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EuEntityPermissionSubjectEntityDetails {
    private SubjectDetailsType subjectDetailsType;
    private String firstName;
    private String address;
    private String fullName;

    public EuEntityPermissionSubjectEntityDetails() {
    }

    public enum SubjectDetailsType {

        ENTITY_BY_FINGERPRINT("EntityByFingerprint");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        SubjectDetailsType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SubjectDetailsType fromValue(String value) {
            for (SubjectDetailsType b : SubjectDetailsType.values()) {
                if (b.value.equalsIgnoreCase(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}