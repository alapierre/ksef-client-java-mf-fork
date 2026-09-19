package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CollectiveIdentifierInvoicesQueryResponse {
    private String continuationToken;
    private List<CollectiveIdentifierInvoicesQueryResponseItem> invoices = new ArrayList<>();

    public CollectiveIdentifierInvoicesQueryResponse() {
    }

    public CollectiveIdentifierInvoicesQueryResponse(String continuationToken, List<CollectiveIdentifierInvoicesQueryResponseItem> invoices) {
        this.continuationToken = continuationToken;
        this.invoices = invoices;
    }

}
