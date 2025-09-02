package pl.akmf.ksef.sdk.client.model.invoice;

import java.util.ArrayList;
import java.util.List;

public class QueryInvoiceMetadataResponse {

    private Integer totalCount;
    private Boolean hasMore;

    private List<InvoiceMetadata> invoices = new ArrayList<>();

    public QueryInvoiceMetadataResponse() {
    }

    public QueryInvoiceMetadataResponse(Integer totalCount, Boolean hasMore, List<InvoiceMetadata> invoices) {
        this.totalCount = totalCount;
        this.hasMore = hasMore;
        this.invoices = invoices;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public List<InvoiceMetadata> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<InvoiceMetadata> invoices) {
        this.invoices = invoices;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}

