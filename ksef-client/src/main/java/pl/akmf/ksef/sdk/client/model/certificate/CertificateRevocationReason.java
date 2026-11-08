package pl.akmf.ksef.sdk.client.model.certificate;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum CertificateRevocationReason {

    UNSPECIFIED("Unspecified"),
    SUPERSEDED("Superseded"),
    KEYCOMPROMISE("KeyCompromise");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    CertificateRevocationReason(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CertificateRevocationReason fromValue(String value) {
        for (CertificateRevocationReason b : CertificateRevocationReason.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

