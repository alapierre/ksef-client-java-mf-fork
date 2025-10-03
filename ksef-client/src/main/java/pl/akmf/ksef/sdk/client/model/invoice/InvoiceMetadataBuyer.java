package pl.akmf.ksef.sdk.client.model.invoice;


import lombok.Data;

@Data
public class InvoiceMetadataBuyer {
    private BuyerIdentifier identifier;
    private String name;
}

