package pl.akmf.ksef.sdk.client.model.session;

import java.util.List;

public class SessionInvoicesResponse {
    private String continuationToken;
    private List<SessionInvoiceStatusResponse> invoices;
    private Integer totalCount;
    private Boolean hasMore;

    public SessionInvoicesResponse() {
    }

    public SessionInvoicesResponse(String continuationToken, List<SessionInvoiceStatusResponse> invoices, Integer totalCount, Boolean hasMore) {
        this.continuationToken = continuationToken;
        this.invoices = invoices;
        this.totalCount = totalCount;
        this.hasMore = hasMore;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    public List<SessionInvoiceStatusResponse> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<SessionInvoiceStatusResponse> invoices) {
        this.invoices = invoices;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}

