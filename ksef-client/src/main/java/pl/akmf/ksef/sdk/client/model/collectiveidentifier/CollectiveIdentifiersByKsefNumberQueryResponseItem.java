package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Setter
@Getter
public class CollectiveIdentifiersByKsefNumberQueryResponseItem {
    private String collectiveIdentifierNumber;
    private OffsetDateTime dateCreated;
    private Boolean createdInCurrentContext;

    public CollectiveIdentifiersByKsefNumberQueryResponseItem() {
    }

    public CollectiveIdentifiersByKsefNumberQueryResponseItem(String collectiveIdentifierNumber, OffsetDateTime dateCreated, Boolean createdInCurrentContext) {
        this.collectiveIdentifierNumber = collectiveIdentifierNumber;
        this.dateCreated = dateCreated;
        this.createdInCurrentContext = createdInCurrentContext;
    }

}
