package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EuAdministrationSubjectEntityDetails {
    private String fullName;
    private SubjectDetailType subjectDetailType;

    public EuAdministrationSubjectEntityDetails() {
    }

    public EuAdministrationSubjectEntityDetails(final String fullName, final SubjectDetailType subjectDetailType) {
        this.fullName = fullName;
        this.subjectDetailType = subjectDetailType;
    }

    public enum SubjectDetailType {

        ENTITY_BY_IDENTIFIER("EntityByIdentifier");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        SubjectDetailType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static SubjectDetailType fromValue(String value) {
            for (SubjectDetailType b : SubjectDetailType.values()) {
                if (b.value.equalsIgnoreCase(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
