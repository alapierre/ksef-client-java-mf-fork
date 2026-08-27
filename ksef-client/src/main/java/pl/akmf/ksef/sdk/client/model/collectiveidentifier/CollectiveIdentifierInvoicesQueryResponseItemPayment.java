package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

public class CollectiveIdentifierInvoicesQueryResponseItemPayment {
    private Double amount;
    private String currency;

    public CollectiveIdentifierInvoicesQueryResponseItemPayment() {
    }

    public CollectiveIdentifierInvoicesQueryResponseItemPayment(Double amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
