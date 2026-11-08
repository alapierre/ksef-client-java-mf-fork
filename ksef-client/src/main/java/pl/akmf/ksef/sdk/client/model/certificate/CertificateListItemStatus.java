package pl.akmf.ksef.sdk.client.model.certificate;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum CertificateListItemStatus {

    ACTIVE("Active"),

    BLOCKED("Blocked"),

    REVOKED("Revoked"),

    EXPIRED("Expired");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    CertificateListItemStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CertificateListItemStatus fromValue(String value) {
        for (CertificateListItemStatus b : CertificateListItemStatus.values()) {
            if (b.value.equalsIgnoreCase(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

