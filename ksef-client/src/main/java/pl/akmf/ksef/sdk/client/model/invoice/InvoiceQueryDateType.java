package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum InvoiceQueryDateType {

    ISSUE("Issue"),
    INVOICING("Invoicing"),
    PERMANENTSTORAGE("PermanentStorage");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    InvoiceQueryDateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoiceQueryDateType fromValue(String value) {
        for (InvoiceQueryDateType b : InvoiceQueryDateType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
