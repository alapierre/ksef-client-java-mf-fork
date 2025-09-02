package pl.akmf.ksef.sdk.client.model.permission.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SubordinateRoleSubordinateEntityIdentifierType {

    NIP("Nip");

    private String value;

    SubordinateRoleSubordinateEntityIdentifierType(String value) {
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
    public static SubordinateRoleSubordinateEntityIdentifierType fromValue(String value) {
        for (SubordinateRoleSubordinateEntityIdentifierType b : SubordinateRoleSubordinateEntityIdentifierType.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
