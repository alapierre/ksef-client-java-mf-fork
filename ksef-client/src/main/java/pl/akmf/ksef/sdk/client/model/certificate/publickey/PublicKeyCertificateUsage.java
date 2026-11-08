package pl.akmf.ksef.sdk.client.model.certificate.publickey;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum PublicKeyCertificateUsage {
    KSEFTOKENENCRYPTION("KsefTokenEncryption"),
    SYMMETRICKEYENCRYPTION("SymmetricKeyEncryption");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    PublicKeyCertificateUsage(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static PublicKeyCertificateUsage fromValue(String value) {
        for (PublicKeyCertificateUsage b : PublicKeyCertificateUsage.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

