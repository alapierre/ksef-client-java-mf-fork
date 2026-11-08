package pl.akmf.ksef.sdk.client.model.session;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SystemCode {
    FA_2("FA (2)"),
    PEF_3("PEF (3)"),
    KOR_PEF_3("PEF_KOR (3)"),
    FA_3("FA (3)"),
    FA_RR("FA_RR (1)");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SystemCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SystemCode fromValue(String value) {
        for (SystemCode b : SystemCode.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
