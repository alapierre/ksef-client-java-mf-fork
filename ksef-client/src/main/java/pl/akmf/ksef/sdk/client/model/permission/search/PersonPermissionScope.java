package pl.akmf.ksef.sdk.client.model.permission.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PersonPermissionScope {

    CREDENTIALSMANAGE("CredentialsManage"),

    CREDENTIALSREAD("CredentialsRead"),

    INVOICEWRITE("InvoiceWrite"),

    INVOICEREAD("InvoiceRead"),

    INTROSPECTION("Introspection"),

    SUBUNITMANAGE("SubunitManage"),

    ENFORCEMENTOPERATIONS("EnforcementOperations"),

    OWNER("Owner");

    private final String value;

    PersonPermissionScope(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static PersonPermissionScope fromValue(String value) {
        for (PersonPermissionScope b : PersonPermissionScope.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

