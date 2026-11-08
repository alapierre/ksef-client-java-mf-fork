package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SubunitPermissionType {

    CREDENTIALS_MANAGE("CredentialsManage");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SubunitPermissionType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SubunitPermissionType fromValue(String value) {
        for (SubunitPermissionType b : SubunitPermissionType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

