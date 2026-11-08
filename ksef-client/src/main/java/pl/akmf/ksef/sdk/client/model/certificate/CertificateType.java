package pl.akmf.ksef.sdk.client.model.certificate;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum CertificateType {
    AUTHENTICATION("Authentication"),
    OFFLINE("Offline");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    CertificateType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CertificateType fromValue(String value) {
        for (CertificateType b : CertificateType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

