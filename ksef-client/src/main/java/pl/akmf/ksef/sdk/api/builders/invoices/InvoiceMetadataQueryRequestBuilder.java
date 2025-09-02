package pl.akmf.ksef.sdk.api.builders.invoices;

import pl.akmf.ksef.sdk.client.model.invoice.CurrencyCode;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataInvoiceType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataSchema;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryDateRange;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySchemaType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySubjectType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryAmount;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryBuyer;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySeller;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InvoicingMode;

import java.util.List;

public class InvoiceMetadataQueryRequestBuilder {
    private InvoiceQuerySubjectType subjectType;
    private InvoiceQueryDateRange dateRange;
    private String ksefNumber;
    private String invoiceNumber;
    private InvoiceQueryAmount amount;
    private InvoiceQuerySeller seller;
    private InvoiceQueryBuyer buyer;
    private List<CurrencyCode> currencyCodes;
    private Boolean hasAttachment = false;
    private InvoicingMode invoicingMode;
    private Boolean isSelfInvoicing;
    private InvoiceMetadataSchema invoiceSchema;
    private List<InvoiceMetadataInvoiceType> invoiceTypes;
    private InvoiceQuerySchemaType schemaType;

    public InvoiceMetadataQueryRequestBuilder withSubjectType(InvoiceQuerySubjectType subjectType) {
        this.subjectType = subjectType;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withDateRange(InvoiceQueryDateRange dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withKsefNumber(String ksefNumber) {
        this.ksefNumber = ksefNumber;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withAmount(InvoiceQueryAmount amount) {
        this.amount = amount;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withSeller(InvoiceQuerySeller seller) {
        this.seller = seller;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withBuyer(InvoiceQueryBuyer buyer) {
        this.buyer = buyer;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withCurrencyCodes(List<CurrencyCode> currencyCodes) {
        this.currencyCodes = currencyCodes;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withInvoicingMode(InvoicingMode invoicingMode) {
        this.invoicingMode = invoicingMode;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withIsSelfInvoicing(Boolean isSelfInvoicing) {
        this.isSelfInvoicing = isSelfInvoicing;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withInvoiceSchema(InvoiceMetadataSchema invoiceSchema) {
        this.invoiceSchema = invoiceSchema;
        return this;
    }

    public InvoiceMetadataQueryRequestBuilder withInvoiceTypes(List<InvoiceMetadataInvoiceType> invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
        return this;
    }
    public InvoiceMetadataQueryRequestBuilder withSchemaType(InvoiceQuerySchemaType schemaType) {
        this.schemaType = schemaType;
        return this;
    }

    public InvoiceMetadataQueryRequest build() {
        InvoiceMetadataQueryRequest request = new InvoiceMetadataQueryRequest();
        request.setSubjectType(subjectType);
        request.setDateRange(dateRange);
        request.setKsefNumber(ksefNumber);
        request.setInvoiceNumber(invoiceNumber);
        request.setAmount(amount);
        request.setSeller(seller);
        request.setBuyer(buyer);
        request.setCurrencyCodes(currencyCodes);
        request.setInvoicingMode(invoicingMode);
        request.setSelfInvoicing(isSelfInvoicing);
        request.setInvoiceSchema(invoiceSchema);
        request.setInvoiceTypes(invoiceTypes);
        request.setHasAttachment(hasAttachment);
        request.setSchemaType(schemaType);
        return request;
    }
}

