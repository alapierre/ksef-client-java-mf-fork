package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum EntityPermissionItemScope {
    INVOICEWRITE("InvoiceWrite"),

    INVOICEREAD("InvoiceRead");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    EntityPermissionItemScope(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EntityPermissionItemScope fromValue(String value) {
        for (EntityPermissionItemScope b : EntityPermissionItemScope.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
