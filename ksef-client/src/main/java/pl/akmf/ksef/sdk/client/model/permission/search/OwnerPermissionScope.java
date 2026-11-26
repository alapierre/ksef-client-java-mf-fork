package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

/** Uprawnienia właścicielskie zwracane przez KSeF w polu ownerPermissions. */
public enum OwnerPermissionScope {
    CREDENTIALS_MANAGE("CredentialsManage"),
    CREDENTIALS_READ("CredentialsRead"),
    INVOICE_WRITE("InvoiceWrite"),
    INVOICE_READ("InvoiceRead"),
    INTROSPECTION("Introspection"),
    SUBUNIT_MANAGE("SubunitManage"),
    ENFORCEMENT_OPERATIONS("EnforcementOperations"),
    COLLECTIVE_IDENTIFIER_MANAGE("CollectiveIdentifierManage");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    OwnerPermissionScope(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
