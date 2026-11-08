package pl.akmf.ksef.sdk.client.model.session;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum AuthenticationMethod {

    TOKEN("Token"),

    TRUSTEDPROFILE("TrustedProfile"),

    INTERNALCERTIFICATE("InternalCertificate"),

    QUALIFIEDSIGNATURE("QualifiedSignature"),

    QUALIFIEDSEAL("QualifiedSeal"),

    PERSONALSIGNATURE("PersonalSignature"),

    PEPPOL_SIGNATURE("PeppolSignature");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    AuthenticationMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static AuthenticationMethod fromValue(String value) {
        for (AuthenticationMethod b : AuthenticationMethod.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

