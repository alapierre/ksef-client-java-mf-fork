package pl.akmf.ksef.sdk.client.model.permission.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EuEntityPermissionsAuthorIdentifierType {

    NIP("Nip"),

    PESEL("Pesel"),

    FINGERPRINT("Fingerprint");

    private String value;

    EuEntityPermissionsAuthorIdentifierType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EuEntityPermissionsAuthorIdentifierType fromValue(String value) {
        for (EuEntityPermissionsAuthorIdentifierType b : EuEntityPermissionsAuthorIdentifierType.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
