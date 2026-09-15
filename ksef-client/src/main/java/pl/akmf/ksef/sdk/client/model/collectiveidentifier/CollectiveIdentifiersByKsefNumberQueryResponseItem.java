package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import java.time.OffsetDateTime;

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

    public String getCollectiveIdentifierNumber() {
        return collectiveIdentifierNumber;
    }

    public void setCollectiveIdentifierNumber(String collectiveIdentifierNumber) {
        this.collectiveIdentifierNumber = collectiveIdentifierNumber;
    }

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(OffsetDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Boolean getCreatedInCurrentContext() {
        return createdInCurrentContext;
    }

    public void setCreatedInCurrentContext(Boolean createdInCurrentContext) {
        this.createdInCurrentContext = createdInCurrentContext;
    }
}
