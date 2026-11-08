package pl.akmf.ksef.sdk.client.model.permission.indirect;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum IndirectPermissionType {

    INVOICE_WRITE("InvoiceWrite"),

    INVOICE_READ("InvoiceRead");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    IndirectPermissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}

