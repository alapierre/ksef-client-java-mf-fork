package pl.akmf.ksef.sdk.client.model.session;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum CommonSessionStatus {

    SUCCEEDED("Succeeded"),
    INPROGRESS("InProgress"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    CommonSessionStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CommonSessionStatus fromValue(String value) {
        for (CommonSessionStatus b : CommonSessionStatus.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

