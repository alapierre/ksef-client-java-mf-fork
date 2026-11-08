package pl.akmf.ksef.sdk.client.model.auth;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum EncryptionMethod {
    RSA("RSA"),
    ECDSA("ECDSA");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    EncryptionMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static EncryptionMethod fromValue(String value) {
        for (EncryptionMethod b : EncryptionMethod.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unsupported key algorithm:" + value + "'");
    }
}