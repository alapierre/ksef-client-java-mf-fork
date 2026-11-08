package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum ThirdSubjectIdentifierType {

    INTERNAL_ID("InternalId"),

    NONE("None"),

    OTHER("Other"),

    NIP("Nip"),

    VATUE("VatUe");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    ThirdSubjectIdentifierType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ThirdSubjectIdentifierType fromValue(String value) {
        for (ThirdSubjectIdentifierType b : ThirdSubjectIdentifierType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
