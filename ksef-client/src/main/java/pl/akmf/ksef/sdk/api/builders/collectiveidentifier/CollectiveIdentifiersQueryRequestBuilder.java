package pl.akmf.ksef.sdk.api.builders.collectiveidentifier;

import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifiersQueryRequest;

import java.time.OffsetDateTime;

public class CollectiveIdentifiersQueryRequestBuilder {
    private String collectiveIdentifierNumber;
    private OffsetDateTime dateCreatedFrom;
    private OffsetDateTime dateCreatedTo;
    private Integer invoiceCountFrom;
    private Integer invoiceCountTo;
    private Boolean createdInCurrentContext;

    public CollectiveIdentifiersQueryRequestBuilder withCollectiveIdentifierNumber(String collectiveIdentifierNumber) {
        this.collectiveIdentifierNumber = collectiveIdentifierNumber;
        return this;
    }

    public CollectiveIdentifiersQueryRequestBuilder withDateCreatedFrom(OffsetDateTime dateCreatedFrom) {
        this.dateCreatedFrom = dateCreatedFrom;
        return this;
    }

    public CollectiveIdentifiersQueryRequestBuilder withDateCreatedTo(OffsetDateTime dateCreatedTo) {
        this.dateCreatedTo = dateCreatedTo;
        return this;
    }

    public CollectiveIdentifiersQueryRequestBuilder withInvoiceCountFrom(Integer invoiceCountFrom) {
        this.invoiceCountFrom = invoiceCountFrom;
        return this;
    }

    public CollectiveIdentifiersQueryRequestBuilder withInvoiceCountTo(Integer invoiceCountTo) {
        this.invoiceCountTo = invoiceCountTo;
        return this;
    }

    public CollectiveIdentifiersQueryRequestBuilder withCreatedInCurrentContext(Boolean createdInCurrentContext) {
        this.createdInCurrentContext = createdInCurrentContext;
        return this;
    }

    public CollectiveIdentifiersQueryRequest build() {
        return new CollectiveIdentifiersQueryRequest(collectiveIdentifierNumber, dateCreatedFrom, dateCreatedTo, invoiceCountFrom, invoiceCountTo, createdInCurrentContext);
    }
}
