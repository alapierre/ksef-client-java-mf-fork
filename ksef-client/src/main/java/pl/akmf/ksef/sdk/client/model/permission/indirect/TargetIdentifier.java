package pl.akmf.ksef.sdk.client.model.permission.indirect;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetIdentifier {
    private IdentifierType type;
    private String value;

    public TargetIdentifier(IdentifierType type, String value) {
        this.type = type;
        this.value = value;
    }

    public enum IdentifierType {
        ALL_PARTNERS("AllPartners"),
        INTERNAL_ID("InternalId"),
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
