package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum AmountType {

    BRUTTO("Brutto"),

    NETTO("Netto"),

    VAT("Vat");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    AmountType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AmountType fromValue(String value) {
        for (AmountType b : AmountType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
