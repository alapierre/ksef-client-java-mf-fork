package pl.akmf.ksef.sdk.client.model.permission.person;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum PersonPermissionSubjectDetailsType {

    PERSON_BY_IDENTIFIER("PersonByIdentifier"),
    PERSON_BY_FINGERPRINT_WITH_IDENTIFIER("PersonByFingerprintWithIdentifier"),
    PERSON_BY_FINGERPRINT_WITHOUT_IDENTIFIER("PersonByFingerprintWithoutIdentifier");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    PersonPermissionSubjectDetailsType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static PersonPermissionSubjectDetailsType fromValue(String value) {
        for (PersonPermissionSubjectDetailsType b : PersonPermissionSubjectDetailsType.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
