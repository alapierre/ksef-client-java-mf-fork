package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import java.util.ArrayList;
import java.util.List;

public class GenerateCollectiveIdentifierRequest {
    private List<CollectiveIdentifierInvoice> invoices = new ArrayList<>();

    public GenerateCollectiveIdentifierRequest() {
    }

    public GenerateCollectiveIdentifierRequest(List<CollectiveIdentifierInvoice> invoices) {
        this.invoices = invoices;
    }

    public List<CollectiveIdentifierInvoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<CollectiveIdentifierInvoice> invoices) {
        this.invoices = invoices;
    }
}
