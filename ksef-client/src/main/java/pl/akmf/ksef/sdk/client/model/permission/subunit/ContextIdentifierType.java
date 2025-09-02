package pl.akmf.ksef.sdk.client.model.permission.subunit;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ContextIdentifierType {
    NIP("Nip"),
    INTERNALID("InternalId");

    private final String value;

    ContextIdentifierType(String value) {
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
}
