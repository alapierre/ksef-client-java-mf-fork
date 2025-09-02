package pl.akmf.ksef.sdk.client.model.invoice;

/**
 * Dane nabywcy.
 */
public class InvoiceQueryBuyer {
    private BuyerIdentifierType identifierType;

    private String identifier;


    public InvoiceQueryBuyer() {
    }

    public BuyerIdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(BuyerIdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}

