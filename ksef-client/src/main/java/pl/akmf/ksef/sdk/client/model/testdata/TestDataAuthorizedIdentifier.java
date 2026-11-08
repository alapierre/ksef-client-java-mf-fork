package pl.akmf.ksef.sdk.client.model.testdata;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDataAuthorizedIdentifier {
    private TestDataAuthorizedIdentifierType type;
    private String value;

    public TestDataAuthorizedIdentifier(TestDataAuthorizedIdentifierType type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum TestDataAuthorizedIdentifierType {

        NIP("Nip"),

        PESEL("Pesel"),

        FINGERPRINT("Fingerprint");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        TestDataAuthorizedIdentifierType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static TestDataAuthorizedIdentifierType fromValue(String value) {
            for (TestDataAuthorizedIdentifierType b : TestDataAuthorizedIdentifierType.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}

