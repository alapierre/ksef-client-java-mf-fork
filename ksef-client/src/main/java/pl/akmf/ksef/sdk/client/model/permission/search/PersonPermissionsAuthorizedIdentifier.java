package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonPermissionsAuthorizedIdentifier {
    private IdentifierType type;
    private String value;

    public PersonPermissionsAuthorizedIdentifier() {
    }

    public PersonPermissionsAuthorizedIdentifier(IdentifierType type, String value) {
        this.type = type;
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

        @JsonCreator
        public static IdentifierType fromValue(String value) {
            for (IdentifierType b : IdentifierType.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}

