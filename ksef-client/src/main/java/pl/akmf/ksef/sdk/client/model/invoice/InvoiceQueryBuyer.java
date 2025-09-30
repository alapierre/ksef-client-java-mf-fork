package pl.akmf.ksef.sdk.client.model.invoice;

/**
 * Dane nabywcy.
 */
public class InvoiceQueryBuyer {
    private IdentifierType identifierType;
    private String identifier;

    public InvoiceQueryBuyer() {
    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(IdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}

