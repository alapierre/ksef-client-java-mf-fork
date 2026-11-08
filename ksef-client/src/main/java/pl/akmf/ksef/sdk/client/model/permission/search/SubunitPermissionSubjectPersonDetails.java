package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SubunitPermissionSubjectPersonDetails {
    private SubjectDetailsType subjectDetailsType;
    private String firstName;
    private String lastName;
    private PersonIdentifier personIdentifier;
    private LocalDate birthDate;
    private IdDocument idDocument;

    public SubunitPermissionSubjectPersonDetails() {
    }

    public enum SubjectDetailsType {
        PERSON_BY_IDENTIFIER("PersonByIdentifier"),

        PERSON_BY_FINGERPRINT_WITH_IDENTIFIER("PersonByFingerprintWithIdentifier"),

        PERSON_BY_FINGERPRINT_WITHOUT_IDENTIFIER("PersonByFingerprintWithoutIdentifier");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        SubjectDetailsType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SubjectDetailsType fromValue(String value) {
            for (SubjectDetailsType b : SubjectDetailsType.values()) {
                if (b.value.equalsIgnoreCase(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    public enum PersonIdentifier {

        NIP("Nip"),

        PESEL("Pesel");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        PersonIdentifier(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static PersonIdentifier fromValue(String value) {
            for (PersonIdentifier b : PersonIdentifier.values()) {
                if (b.value.equalsIgnoreCase(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    @Getter
    @Setter
    public static class IdDocument {
        private String type;
        private String number;
        private String country;

        public IdDocument() {
        }

        public IdDocument(String type, String number, String country) {
            this.type = type;
            this.number = number;
            this.country = country;
        }
    }
}