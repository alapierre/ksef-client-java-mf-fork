package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

public class CollectiveIdentifierInvoicesQueryResponseItem {
    private String ksefNumber;
    private CollectiveIdentifierInvoicesQueryResponseItemPayment payment;
    private String description;
    private Boolean detailsHidden;

    public CollectiveIdentifierInvoicesQueryResponseItem() {
    }

    public CollectiveIdentifierInvoicesQueryResponseItem(String ksefNumber, CollectiveIdentifierInvoicesQueryResponseItemPayment payment, String description, Boolean detailsHidden) {
        this.ksefNumber = ksefNumber;
        this.payment = payment;
        this.description = description;
        this.detailsHidden = detailsHidden;
    }

    public String getKsefNumber() {
        return ksefNumber;
    }

    public void setKsefNumber(String ksefNumber) {
        this.ksefNumber = ksefNumber;
    }

    public CollectiveIdentifierInvoicesQueryResponseItemPayment getPayment() {
        return payment;
    }

    public void setPayment(CollectiveIdentifierInvoicesQueryResponseItemPayment payment) {
        this.payment = payment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getDetailsHidden() {
        return detailsHidden;
    }

    public void setDetailsHidden(Boolean detailsHidden) {
        this.detailsHidden = detailsHidden;
    }
}
