package pl.akmf.ksef.sdk.client.model.session;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SchemaVersion {
    VERSION_1_0E("1-0E"),
    VERSION_1_1E("1-1E"),
    VERSION_2_1("2-1");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SchemaVersion(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SchemaVersion fromValue(String value) {
        for (SchemaVersion b : SchemaVersion.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}