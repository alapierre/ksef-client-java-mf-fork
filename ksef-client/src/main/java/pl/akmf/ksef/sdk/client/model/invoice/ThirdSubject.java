package pl.akmf.ksef.sdk.client.model.invoice;

public class ThirdSubject {
    private IdentifierType identifierType;
    private String identifier;
    private String name;
    private int role;

    public ThirdSubject(final IdentifierType identifierType,
                        final String identifier,
                        final String name,
                        final int role) {
        this.identifierType = identifierType;
        this.identifier = identifier;
        this.name = name;
        this.role = role;
    }

    public ThirdSubject() {

    }

    public IdentifierType getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(final IdentifierType identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getRole() {
        return role;
    }

    public void setRole(final int role) {
        this.role = role;
    }
}
