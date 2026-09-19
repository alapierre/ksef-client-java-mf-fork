package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class CollectiveIdentifiersQueryRequest {
    private String collectiveIdentifierNumber;
    private OffsetDateTime dateCreatedFrom;
    private OffsetDateTime dateCreatedTo;
    private Integer invoiceCountFrom;
    private Integer invoiceCountTo;
    private Boolean createdInCurrentContext;

    public CollectiveIdentifiersQueryRequest() {
    }

    public CollectiveIdentifiersQueryRequest(String collectiveIdentifierNumber, OffsetDateTime dateCreatedFrom, OffsetDateTime dateCreatedTo, Integer invoiceCountFrom, Integer invoiceCountTo, Boolean createdInCurrentContext) {
        this.collectiveIdentifierNumber = collectiveIdentifierNumber;
        this.dateCreatedFrom = dateCreatedFrom;
        this.dateCreatedTo = dateCreatedTo;
        this.invoiceCountFrom = invoiceCountFrom;
        this.invoiceCountTo = invoiceCountTo;
        this.createdInCurrentContext = createdInCurrentContext;
    }

}
