package pl.akmf.ksef.sdk.client.model.invoice;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Gets or Sets InvoiceMetadataInvoiceType
 */
public enum InvoiceMetadataInvoiceType {

    VAT("Vat"),// Faktura podstawowa

    KOR("Kor"),// Faktura korygująca

    ZAL("Zal"),// Faktura zaliczkowa

    ROZ("Roz"),// Faktura rozliczeniowa

    UPR("Upr"),// Faktura uproszczona

    KOR_ZAL("KorZal"),// Korygująca do faktury zaliczkowej

    KOR_ROZ("KorRoz");// Korygująca do faktury rozliczeniowej

    private String value;

    InvoiceMetadataInvoiceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static InvoiceMetadataInvoiceType fromValue(String value) {
        for (InvoiceMetadataInvoiceType b : InvoiceMetadataInvoiceType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}

