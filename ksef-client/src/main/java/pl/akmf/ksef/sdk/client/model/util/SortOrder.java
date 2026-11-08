package pl.akmf.ksef.sdk.client.model.util;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SortOrder {
    ASC("Asc"),
    DESC("Desc");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SortOrder(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SortOrder fromValue(String value) {
        for (SortOrder b : SortOrder.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
