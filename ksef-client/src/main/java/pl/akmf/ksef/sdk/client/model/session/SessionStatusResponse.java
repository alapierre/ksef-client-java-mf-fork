package pl.akmf.ksef.sdk.client.model.session;

import pl.akmf.ksef.sdk.client.model.StatusInfo;

import java.time.OffsetDateTime;

public class SessionStatusResponse {
    private StatusInfo status;
    private OffsetDateTime validUntil;
    private UpoResponse upo;
    private Integer invoiceCount;
    private Integer successfulInvoiceCount;
    private Integer failedInvoiceCount;

    public SessionStatusResponse() {
    }

    public SessionStatusResponse(StatusInfo status, UpoResponse upo, Integer invoiceCount, Integer successfulInvoiceCount, Integer failedInvoiceCount) {
        this.status = status;
        this.upo = upo;
        this.invoiceCount = invoiceCount;
        this.successfulInvoiceCount = successfulInvoiceCount;
        this.failedInvoiceCount = failedInvoiceCount;
    }

    public StatusInfo getStatus() {
        return status;
    }

    public void setStatus(StatusInfo status) {
        this.status = status;
    }

    public OffsetDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(OffsetDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public UpoResponse getUpo() {
        return upo;
    }

    public void setUpo(UpoResponse upo) {
        this.upo = upo;
    }

    public Integer getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(Integer invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    public Integer getSuccessfulInvoiceCount() {
        return successfulInvoiceCount;
    }

    public void setSuccessfulInvoiceCount(Integer successfulInvoiceCount) {
        this.successfulInvoiceCount = successfulInvoiceCount;
    }

    public Integer getFailedInvoiceCount() {
        return failedInvoiceCount;
    }

    public void setFailedInvoiceCount(Integer failedInvoiceCount) {
        this.failedInvoiceCount = failedInvoiceCount;
    }
}

