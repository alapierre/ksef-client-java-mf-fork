package pl.akmf.ksef.sdk.client.model.lighthouse;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum Categories {
    FAILURE("01"),
    TOTAL_FAILURE("02"),
    PLANNED_UNAVAILABILITY("03");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    Categories(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}