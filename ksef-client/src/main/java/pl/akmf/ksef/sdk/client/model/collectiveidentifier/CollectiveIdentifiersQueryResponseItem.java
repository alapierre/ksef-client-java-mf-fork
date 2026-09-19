package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class CollectiveIdentifiersQueryResponseItem {
    private String collectiveIdentifierNumber;
    private OffsetDateTime dateCreated;
    private Integer invoiceCount;
    private Boolean createdInCurrentContext;

    public CollectiveIdentifiersQueryResponseItem() {
    }

    public CollectiveIdentifiersQueryResponseItem(String collectiveIdentifierNumber, OffsetDateTime dateCreated, Integer invoiceCount, Boolean createdInCurrentContext) {
        this.collectiveIdentifierNumber = collectiveIdentifierNumber;
        this.dateCreated = dateCreated;
        this.invoiceCount = invoiceCount;
        this.createdInCurrentContext = createdInCurrentContext;
    }

}
