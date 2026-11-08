package pl.akmf.ksef.sdk.client.model.invoice;

import io.alapierre.ksef.json.annotation.JsonName;
import lombok.Getter;
import lombok.Setter;
import pl.akmf.ksef.sdk.client.model.StatusInfo;

import java.time.OffsetDateTime;

@Getter
@Setter
public class InvoiceExportStatus {
    private StatusInfo status;
    private OffsetDateTime completedDate;
    @JsonName("package")
    private InvoiceExportPackage packageParts;
    private OffsetDateTime packageExpirationDate;

    public InvoiceExportStatus() {
    }
}

