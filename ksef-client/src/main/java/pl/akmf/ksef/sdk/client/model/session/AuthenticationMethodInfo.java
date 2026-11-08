package pl.akmf.ksef.sdk.client.model.session;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationMethodInfo {
    private AuthenticationMethodInfoCategory category;
    private String code;
    private String displayName;

    public AuthenticationMethodInfo() {
    }

    public AuthenticationMethodInfo(AuthenticationMethodInfoCategory category, String code, String displayName) {
        this.category = category;
        this.code = code;
        this.displayName = displayName;
    }

    public enum AuthenticationMethodInfoCategory {

        //Uwierzytelnienie podpisem Xades.
        XADES_SIGNATURE("XadesSignature"),

        //Uwierzytelnienie za pomocą Węzła Krajowego (login.gov.pl).
        NATIONAL_NODE("NationalNode"),

        //Uwierzytelnienie tokenem.
        TOKEN("Token"),

        //Uwierzytelnienie inną metodą.
        OTHER("Other");

        @Getter(onMethod_ = @JsonValue)
        private final String value;

        AuthenticationMethodInfoCategory(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static AuthenticationMethodInfoCategory fromValue(String value) {
            for (AuthenticationMethodInfoCategory b : AuthenticationMethodInfoCategory.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
