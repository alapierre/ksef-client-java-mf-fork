package pl.akmf.ksef.sdk.client.model.invoice;

import java.util.ArrayList;
import java.util.List;

public class QueryInvoiceMetadataResponse {
    private Boolean hasMore;
    private List<InvoiceSummary> invoices = new ArrayList<>();

    public QueryInvoiceMetadataResponse() {
    }

    public QueryInvoiceMetadataResponse(Boolean hasMore, List<InvoiceSummary> invoices) {
        this.hasMore = hasMore;
        this.invoices = invoices;
    }

    public List<InvoiceSummary> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceSummary> invoices) {
        this.invoices = invoices;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}

