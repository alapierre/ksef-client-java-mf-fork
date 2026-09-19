package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectiveIdentifierInvoicesQueryResponseItem {
    private String ksefNumber;
    private String collectiveIdentifierNumber;
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

    public CollectiveIdentifierInvoicesQueryResponseItem(String ksefNumber, String collectiveIdentifierNumber, CollectiveIdentifierInvoicesQueryResponseItemPayment payment, String description, Boolean detailsHidden) {
        this.ksefNumber = ksefNumber;
        this.collectiveIdentifierNumber = collectiveIdentifierNumber;
        this.payment = payment;
        this.description = description;
        this.detailsHidden = detailsHidden;
    }

}
