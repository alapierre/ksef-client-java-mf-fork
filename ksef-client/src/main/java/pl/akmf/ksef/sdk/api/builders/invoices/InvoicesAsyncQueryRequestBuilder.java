package pl.akmf.ksef.sdk.api.builders.invoices;

import pl.akmf.ksef.sdk.client.model.invoice.CurrencyCode;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataInvoiceType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryAmount;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryBuyer;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryDateRange;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySchemaType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySeller;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQuerySubjectType;
import pl.akmf.ksef.sdk.client.model.invoice.InvoicesAsyncQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InvoicingMode;
import pl.akmf.ksef.sdk.client.model.session.EncryptionInfo;

import java.util.List;

public class InvoicesAsyncQueryRequestBuilder {
    private EncryptionInfo encryption;
    private InvoiceQuerySubjectType subjectType;
    private InvoiceQueryDateRange dateRange;
    private String ksefNumber;
    private String invoiceNumber;
    private InvoiceQueryAmount amount;
    private InvoiceQuerySeller seller;
    private InvoiceQueryBuyer buyer;
    private List<CurrencyCode> currencyCodes;
    private InvoicingMode invoicingMode;
    private Boolean isSelfInvoicing;
    private InvoiceQuerySchemaType schemaType;
    private List<InvoiceMetadataInvoiceType> invoiceTypes;

    public InvoicesAsyncQueryRequestBuilder withEncryption(EncryptionInfo encryption) {
        this.encryption = encryption;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withSubjectType(InvoiceQuerySubjectType subjectType) {
        this.subjectType = subjectType;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withDateRange(InvoiceQueryDateRange dateRange) {
        this.dateRange = dateRange;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withKsefNumber(String ksefNumber) {
        this.ksefNumber = ksefNumber;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withAmount(InvoiceQueryAmount amount) {
        this.amount = amount;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withSeller(InvoiceQuerySeller seller) {
        this.seller = seller;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withBuyer(InvoiceQueryBuyer buyer) {
        this.buyer = buyer;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withCurrencyCodes(List<CurrencyCode> currencyCodes) {
        this.currencyCodes = currencyCodes;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withInvoicingMode(InvoicingMode invoicingMode) {
        this.invoicingMode = invoicingMode;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withIsSelfInvoicing(Boolean isSelfInvoicing) {
        this.isSelfInvoicing = isSelfInvoicing;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withSchemaType(InvoiceQuerySchemaType schemaType) {
        this.schemaType = schemaType;
        return this;
    }

    public InvoicesAsyncQueryRequestBuilder withInvoiceTypes(List<InvoiceMetadataInvoiceType> invoiceTypes) {
        this.invoiceTypes = invoiceTypes;
        return this;
    }

    public InvoicesAsyncQueryRequest build() {
        InvoicesAsyncQueryRequest request = new InvoicesAsyncQueryRequest();
        request.setEncryption(encryption);
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
        request.setSchemaType(schemaType);
        request.setInvoiceTypes(invoiceTypes);
        return request;
    }
}

