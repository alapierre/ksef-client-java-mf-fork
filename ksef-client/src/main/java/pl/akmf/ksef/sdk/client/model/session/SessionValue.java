package pl.akmf.ksef.sdk.client.model.session;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SessionValue {
    FA("FA"),
    FA_PEF("PEF"),
    @Deprecated
    RR("RR"),
    FA_RR("FA_RR");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SessionValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SessionValue fromValue(String value) {
        for (SessionValue b : SessionValue.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
