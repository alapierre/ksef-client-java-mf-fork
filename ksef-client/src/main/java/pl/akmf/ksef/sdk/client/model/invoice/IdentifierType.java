package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum IdentifierType {

    NONE("None"),
    OTHER("Other"),
    NIP("Nip"),
    VATUE("VatUe");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    IdentifierType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static IdentifierType fromValue(String value) {
        for (IdentifierType b : IdentifierType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

