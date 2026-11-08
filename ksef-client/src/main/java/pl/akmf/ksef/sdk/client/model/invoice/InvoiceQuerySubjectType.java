package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum InvoiceQuerySubjectType {

    SUBJECT1("Subject1"),

    SUBJECT2("Subject2"),

    SUBJECT3("Subject3"),

    SUBJECTAUTHORIZED("SubjectAuthorized");

    @Getter(onMethod_ = @JsonValue)
    private final String value;

    InvoiceQuerySubjectType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoiceQuerySubjectType fromValue(String value) {
        for (InvoiceQuerySubjectType b : InvoiceQuerySubjectType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

