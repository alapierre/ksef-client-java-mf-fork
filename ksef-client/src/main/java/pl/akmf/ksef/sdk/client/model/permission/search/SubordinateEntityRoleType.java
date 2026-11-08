package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SubordinateEntityRoleType {

    LOCAL_GOVERNMENT_SUBUNIT("LocalGovernmentSubUnit"),

    VAT_GROUP_SUBUNIT("VatGroupSubUnit");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SubordinateEntityRoleType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SubordinateEntityRoleType fromValue(String value) {
        for (SubordinateEntityRoleType b : SubordinateEntityRoleType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

