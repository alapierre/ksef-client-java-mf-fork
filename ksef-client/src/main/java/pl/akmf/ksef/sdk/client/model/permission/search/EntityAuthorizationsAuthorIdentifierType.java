package pl.akmf.ksef.sdk.client.model.permission.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EntityAuthorizationsAuthorIdentifierType {

    NIP("Nip"),

    PESEL("Pesel"),

    FINGERPRINT("Fingerprint");

    private String value;

    EntityAuthorizationsAuthorIdentifierType(String value) {
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
    public static EntityAuthorizationsAuthorIdentifierType fromValue(String value) {
        for (EntityAuthorizationsAuthorIdentifierType b : EntityAuthorizationsAuthorIdentifierType.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
