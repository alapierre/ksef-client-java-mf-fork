package pl.akmf.ksef.sdk.client.model.invoice;

import lombok.Data;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 3.10.2025
 */
@Data
public class BuyerIdentifier {
    private IdentifierType type;
    private String value;
}
