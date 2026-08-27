package pl.akmf.ksef.sdk.api.builders.collectiveidentifier;

import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifierInvoice;
import pl.akmf.ksef.sdk.client.model.collectiveidentifier.GenerateCollectiveIdentifierRequest;

import java.util.ArrayList;
import java.util.List;

public class GenerateCollectiveIdentifierRequestBuilder {
    private List<CollectiveIdentifierInvoice> invoices = new ArrayList<>();

    public GenerateCollectiveIdentifierRequestBuilder withInvoices(List<CollectiveIdentifierInvoice> invoices) {
        this.invoices = invoices;
        return this;
    }

    public GenerateCollectiveIdentifierRequestBuilder addInvoice(CollectiveIdentifierInvoice invoice) {
        if (this.invoices == null) {
            this.invoices = new ArrayList<>();
        }
        this.invoices.add(invoice);
        return this;
    }

    public GenerateCollectiveIdentifierRequest build() {
        return new GenerateCollectiveIdentifierRequest(invoices);
    }
}
