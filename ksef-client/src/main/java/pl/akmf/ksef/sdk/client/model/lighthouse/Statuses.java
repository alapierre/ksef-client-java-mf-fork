package pl.akmf.ksef.sdk.client.model.lighthouse;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

// Statusy systemu KSeF zwracane przez Latarnię.
public enum Statuses {

    FULL_AVAILABILITY(0),

    ONGOING_UNAVAILABILITY(100),

    ONGOING_FAILURE(500),

    ONGOING_TOTAL_FAILURE(900);

    @Getter(onMethod_ = @JsonValue)
    private final int value;

    Statuses(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
