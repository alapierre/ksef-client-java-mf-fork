package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import java.util.ArrayList;
import java.util.List;

public class CollectiveIdentifierInvoicesQueryResponse {
    private String continuationToken;
    private List<CollectiveIdentifierInvoicesQueryResponseItem> invoices = new ArrayList<>();

    public CollectiveIdentifierInvoicesQueryResponse() {
    }

    public CollectiveIdentifierInvoicesQueryResponse(String continuationToken, List<CollectiveIdentifierInvoicesQueryResponseItem> invoices) {
        this.continuationToken = continuationToken;
        this.invoices = invoices;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public List<CollectiveIdentifierInvoicesQueryResponseItem> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<CollectiveIdentifierInvoicesQueryResponseItem> invoices) {
        this.invoices = invoices;
    }
}
