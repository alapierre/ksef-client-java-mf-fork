package pl.akmf.ksef.sdk.client.model.auth;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum TokenPermissionType {
    INVOICE_READ("InvoiceRead"),
    INVOICE_WRITE("InvoiceWrite"),
    SUBUNIT_MANAGE("SubunitManage"),
    ENFORCEMENT_OPERATION("EnforcementOperations"),
    PEPPOL_ID("PeppolId"),
    INTROSPECTION("Introspection"),
    CREDENTIALS_READ("CredentialsRead"),
    CREDENTIALS_MANAGE("CredentialsManage"),
    COLLECTIVE_IDENTIFIER_MANAGE("CollectiveIdentifierManage");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    TokenPermissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static TokenPermissionType fromValue(String value) {
        for (TokenPermissionType b : TokenPermissionType.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
