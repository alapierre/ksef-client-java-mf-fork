package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum InvoicingMode {

    ONLINE("Online"),

    OFFLINE("Offline");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    InvoicingMode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoicingMode fromValue(String value) {
        for (InvoicingMode b : InvoicingMode.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

