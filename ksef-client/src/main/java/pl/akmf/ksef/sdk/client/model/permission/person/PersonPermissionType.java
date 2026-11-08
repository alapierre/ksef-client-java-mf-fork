package pl.akmf.ksef.sdk.client.model.permission.person;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

/**
 * Gets or Sets PersonPermissionType
 */
public enum PersonPermissionType {

    CREDENTIALSMANAGE("CredentialsManage"),

    CREDENTIALSREAD("CredentialsRead"),

    INVOICEWRITE("InvoiceWrite"),

    INVOICEREAD("InvoiceRead"),

    INTROSPECTION("Introspection"),

    SUBUNITMANAGE("SubunitManage"),

    ENFORCEMENTOPERATIONS("EnforcementOperations"),

    COLLECTIVEIDENTIFIERMANAGE("CollectiveIdentifierManage");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    PersonPermissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static PersonPermissionType fromValue(String value) {
        for (PersonPermissionType b : PersonPermissionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

