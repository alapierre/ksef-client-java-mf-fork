package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum EuEntityPermissionsQueryPermissionType {

    VATUEMANAGE("VatUeManage"),

    INVOICEWRITE("InvoiceWrite"),

    INVOICEREAD("InvoiceRead"),

    INTROSPECTION("Introspection");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    EuEntityPermissionsQueryPermissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EuEntityPermissionsQueryPermissionType fromValue(String value) {
        for (EuEntityPermissionsQueryPermissionType b : EuEntityPermissionsQueryPermissionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

