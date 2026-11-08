package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityPermissionSubjectEntityDetails {
    private EntityPermissionSubjectDetailsType subjectDetailsType;
    private String fullName;
    private String address;

    public EntityPermissionSubjectEntityDetails() {
    }

    public EntityPermissionSubjectEntityDetails(EntityPermissionSubjectDetailsType subjectDetailsType, String fullName, String address) {
        this.subjectDetailsType = subjectDetailsType;
        this.fullName = fullName;
        this.address = address;
    }

    public enum EntityPermissionSubjectDetailsType {

        ENTITY_BY_IDENTIFIER("EntityByIdentifier"),
        ENTITY_BY_FINGERPRINT("EntityByFingerprint");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        EntityPermissionSubjectDetailsType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static EntityPermissionSubjectDetailsType fromValue(String value) {
            for (EntityPermissionSubjectDetailsType b : EntityPermissionSubjectDetailsType.values()) {
                if (b.value.equalsIgnoreCase(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
