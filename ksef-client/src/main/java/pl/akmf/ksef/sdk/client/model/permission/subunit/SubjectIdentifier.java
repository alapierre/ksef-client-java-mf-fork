package pl.akmf.ksef.sdk.client.model.permission.subunit;

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

    public SubjectIdentifier(IdentifierType subjectIdentifierType, String value) {
        this.type = subjectIdentifierType;
        this.value = value;
    }

    public enum IdentifierType {
        NIP("Nip"),
        PESEL("Pesel"),
        FINGERPRINT("Fingerprint");

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

