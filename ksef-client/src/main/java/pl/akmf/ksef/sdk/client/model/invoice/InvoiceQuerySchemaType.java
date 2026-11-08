package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum InvoiceQuerySchemaType {

    FA_1("FA1"),

    FA_2("FA2"),

    FA_3("FA3");

    @Getter(onMethod_ = @JsonValue)
    private String value;

    InvoiceQuerySchemaType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoiceQuerySchemaType fromValue(String value) {
        for (InvoiceQuerySchemaType b : InvoiceQuerySchemaType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
