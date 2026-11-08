package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum QueryPersonalPermissionTypes {

    CREDENTIAL_MANAGE("CredentialsManage"),
    CREDENTIAL_READ("CredentialsRead"),
    INVOICE_WRITE("InvoiceWrite"),
    INVOICE_READ("InvoiceRead"),
    INTROSPECTION("Introspection"),
    SUBUNIT_MANAGE("SubunitManage"),
    ENFORCEMENT_OPERATION("EnforcementOperations"),
    VAT_UE_MANAGE("VatUeManage"),
    COLLECTIVE_IDENTIFIER_MANAGE("CollectiveIdentifierManage");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    QueryPersonalPermissionTypes(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static QueryPersonalPermissionTypes fromValue(String value) {
        for (QueryPersonalPermissionTypes b : QueryPersonalPermissionTypes.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
