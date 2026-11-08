package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum InvoicePermissionType {

    PEF_INVOICING("PefInvoicing"),
    SELF_INVOICING("SelfInvoicing"),
    TAX_REPRESENTATIVE("TaxRepresentative"),
    RR_INVOICING("RRInvoicing");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    InvoicePermissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoicePermissionType fromValue(String value) {
        for (InvoicePermissionType b : InvoicePermissionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

