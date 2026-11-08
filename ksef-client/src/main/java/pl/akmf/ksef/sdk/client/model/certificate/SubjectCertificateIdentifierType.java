package pl.akmf.ksef.sdk.client.model.certificate;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum SubjectCertificateIdentifierType {

    FINGERPRINT("Fingerprint"),

    PESEL("Pesel"),

    NIP("Nip");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    SubjectCertificateIdentifierType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static SubjectCertificateIdentifierType fromValue(String value) {
        for (SubjectCertificateIdentifierType b : SubjectCertificateIdentifierType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}