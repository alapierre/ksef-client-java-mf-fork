package pl.akmf.ksef.sdk.client.model.testdata;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestDataContextIdentifier {
    private ContextIdentifierType type;
    private String value;

    public TestDataContextIdentifier(ContextIdentifierType type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum ContextIdentifierType {

        NIP("Nip"),
        INTERNAL_ID("InternalId"),
        NIP_VAT_UE("NipVatUe"),
        PEPPOL_ID("PeppolId");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        ContextIdentifierType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static ContextIdentifierType fromValue(String value) {
            for (ContextIdentifierType b : ContextIdentifierType.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
