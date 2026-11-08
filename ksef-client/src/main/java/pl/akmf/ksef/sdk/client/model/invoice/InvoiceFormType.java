package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum InvoiceFormType {

    FA("FA"),
    PEF("PEF"),
    @Deprecated
    RR("RR"),
    FA_RR("FA_RR");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    InvoiceFormType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoiceFormType fromValue(String value) {
        for (InvoiceFormType b : InvoiceFormType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

