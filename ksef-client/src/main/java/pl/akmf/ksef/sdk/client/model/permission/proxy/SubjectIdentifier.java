package pl.akmf.ksef.sdk.client.model.permission.proxy;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectIdentifier {
    private IdentifierType type;
    private String value;

    public SubjectIdentifier() {
    }

    public SubjectIdentifier(IdentifierType type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum IdentifierType {
        PEPPOL_ID("PeppolId"),
        NIP("Nip");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        IdentifierType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}

