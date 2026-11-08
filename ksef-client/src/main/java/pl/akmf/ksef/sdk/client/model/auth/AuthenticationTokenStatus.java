package pl.akmf.ksef.sdk.client.model.auth;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum AuthenticationTokenStatus {
    PENDING("Pending"),
    ACTIVE("Active"),
    REVOKING("Revoking"),
    REVOKED("Revoked"),
    FAILED("Failed");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    AuthenticationTokenStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AuthenticationTokenStatus fromValue(String value) {
        for (AuthenticationTokenStatus b : AuthenticationTokenStatus.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
