package pl.akmf.ksef.sdk.client.model.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.akmf.ksef.sdk.client.model.StatusInfo;

import java.time.OffsetDateTime;
import java.util.List;

public class InvoiceExportStatus {
    private StatusInfo status;
    private OffsetDateTime completedDate;
    @JsonProperty("package")
    private InvoiceExportPackage packageParts;

    public InvoiceExportStatus() {
    }

    public StatusInfo getStatus() {
        return status;
    }
    
    public void setStatus(StatusInfo status) {
        this.status = status;
    }
    
    public OffsetDateTime getCompletedDate() {
        return completedDate;
    }
    
    public void setCompletedDate(OffsetDateTime completedDate) {
        this.completedDate = completedDate;
    }
    
    public InvoiceExportPackage getPackageParts() {
        return packageParts;
    }
    
    public void setPackageParts(InvoiceExportPackage packageParts) {
        this.packageParts = packageParts;
    }
}

