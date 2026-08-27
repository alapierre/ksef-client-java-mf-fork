package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import pl.akmf.ksef.sdk.client.model.invoice.CurrencyCode;

public class CollectiveIdentifierInvoicePayment {
    private Double amount;
    private CurrencyCode currency;

    public CollectiveIdentifierInvoicePayment() {
    }

    public CollectiveIdentifierInvoicePayment(Double amount, CurrencyCode currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }
}
