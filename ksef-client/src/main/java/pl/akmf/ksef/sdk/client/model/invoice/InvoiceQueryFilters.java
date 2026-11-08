package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonName;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
public class InvoiceQueryFilters {
    private InvoiceQuerySubjectType subjectType;
    private InvoiceQueryDateRange dateRange;
    private String ksefNumber;
    private String invoiceNumber;
    private InvoiceQueryAmount amount;
    private String sellerNip;
    private InvoiceBuyerIdentifier buyerIdentifier;
    private List<CurrencyCode> currencyCodes;
    private InvoicingMode invoicingMode;
    @JsonName("isSelfInvoicing")
    @Accessors(prefix = "is")
    @Getter(onMethod_ = @JsonName("isSelfInvoicing"))
    @Setter(onMethod_ = @JsonName("isSelfInvoicing"))
    private Boolean isSelfInvoicing;
    private InvoiceFormType formType;
    private List<InvoiceMetadataInvoiceType> invoiceTypes;
    private Boolean hasAttachment;

    public InvoiceQueryFilters() {
    }
}

