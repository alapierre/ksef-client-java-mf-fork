package pl.akmf.ksef.sdk.client.model.testdata;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonName;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDataPermission {
    private String description;
    @JsonName("permissionType")
    private PermissionType permission;

    public TestDataPermission(String description, PermissionType permission) {
        this.description = description;
        this.permission = permission;
    }

    public enum PermissionType {

        INVOICE_READ("InvoiceRead"),
        INVOICE_WRITE("InvoiceWrite"),
        INTROSPECTION("Introspection"),
        CREDENTIAL_READ("CredentialsRead"),
        CREDENTIAL_MANAGE("CredentialsManage"),
        ENFORCEMENT_OPERATION("EnforcementOperations"),
        SUBUNIT_MANAGE("SubunitManage"),
        COLLECTIVE_IDENTIFIER_MANAGE("CollectiveIdentifierManage");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        PermissionType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static PermissionType fromValue(String value) {
            for (PermissionType b : PermissionType.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
