package pl.akmf.ksef.sdk.client.model.qrcode;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum ContextIdentifierType {
    NIP("Nip"),
    INTERNALID("InternalId"),
    NIPVATUE("NipVatUe");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    ContextIdentifierType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ContextIdentifierType fromValue(String value) {
        for (ContextIdentifierType b : ContextIdentifierType.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}