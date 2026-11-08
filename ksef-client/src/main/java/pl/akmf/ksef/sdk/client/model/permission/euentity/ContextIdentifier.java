package pl.akmf.ksef.sdk.client.model.permission.euentity;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContextIdentifier {
    private IdentifierType type;
    private String value;

    public ContextIdentifier() {
    }

    public ContextIdentifier(IdentifierType type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum IdentifierType {
        NIP_VAT_UE("NipVatUe");

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
