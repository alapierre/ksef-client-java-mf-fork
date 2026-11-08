package pl.akmf.ksef.sdk.client.model.limit;

import io.alapierre.ksef.json.annotation.JsonName;
import lombok.Getter;
import lombok.Setter;

// Limity, które mogą zostać zmienione przez endpoint środowiska testowego.
@Getter
@Setter
public class ApiRateLimitsChangeRequest {
    private OnlineSessionRateLimit onlineSession;
    private BatchSessionRateLimit batchSession;
    private InvoiceSendRateLimit invoiceSend;
    private InvoiceStatusRateLimit invoiceStatus;
    private SessionListRateLimit sessionList;
    private SessionInvoiceListRateLimit sessionInvoiceList;
    private SessionMiscRateLimits sessionMisc;
    private InvoiceMetadataRateLimit invoiceMetadata;
    private InvoiceExportRateLimit invoiceExport;
    @JsonName("invoiceExportStatus")
    private InvoiceExportStatusRateLimit invoiceStatusExport;
    private InvoiceDownloadRateLimit invoiceDownload;
    private OtherRateLimit other;
    private CollectiveIdentifierRateLimit collectiveIdentifier;

    public ApiRateLimitsChangeRequest() {
    }

    public ApiRateLimitsChangeRequest(OnlineSessionRateLimit onlineSession, BatchSessionRateLimit batchSession,
                                      InvoiceSendRateLimit invoiceSend, InvoiceStatusRateLimit invoiceStatus,
                                      SessionListRateLimit sessionList, SessionInvoiceListRateLimit sessionInvoiceList,
                                      SessionMiscRateLimits sessionMisc, InvoiceMetadataRateLimit invoiceMetadata,
                                      InvoiceExportRateLimit invoiceExport,
                                      InvoiceExportStatusRateLimit invoiceStatusExport,
                                      InvoiceDownloadRateLimit invoiceDownload, OtherRateLimit other,
                                      CollectiveIdentifierRateLimit collectiveIdentifier) {
        this.onlineSession = onlineSession;
        this.batchSession = batchSession;
        this.invoiceSend = invoiceSend;
        this.invoiceStatus = invoiceStatus;
        this.sessionList = sessionList;
        this.sessionInvoiceList = sessionInvoiceList;
        this.sessionMisc = sessionMisc;
        this.invoiceMetadata = invoiceMetadata;
        this.invoiceExport = invoiceExport;
        this.invoiceStatusExport = invoiceStatusExport;
        this.invoiceDownload = invoiceDownload;
        this.other = other;
        this.collectiveIdentifier = collectiveIdentifier;
    }
}
