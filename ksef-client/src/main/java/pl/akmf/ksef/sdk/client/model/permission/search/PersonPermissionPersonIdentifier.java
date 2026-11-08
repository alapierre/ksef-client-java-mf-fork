package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonPermissionPersonIdentifier {
    private PersonPermissionIdentifierType type;
    public String value;

    public PersonPermissionPersonIdentifier() {
    }

    public PersonPermissionPersonIdentifier(PersonPermissionIdentifierType type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum PersonPermissionIdentifierType {

        NIP("Nip"),
        PESEL("Pesel");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        PersonPermissionIdentifierType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static PersonPermissionIdentifierType fromValue(String value) {
            for (PersonPermissionIdentifierType b : PersonPermissionIdentifierType.values()) {
                if (b.value.equalsIgnoreCase(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
