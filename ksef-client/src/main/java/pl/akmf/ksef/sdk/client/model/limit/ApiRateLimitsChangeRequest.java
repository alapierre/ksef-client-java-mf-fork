package pl.akmf.ksef.sdk.client.model.limit;

import com.fasterxml.jackson.annotation.JsonProperty;

// Limity, które mogą zostać zmienione przez endpoint środowiska testowego.
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
    @JsonProperty("invoiceExportStatus")
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

    public OnlineSessionRateLimit getOnlineSession() {
        return onlineSession;
    }

    public void setOnlineSession(OnlineSessionRateLimit onlineSession) {
        this.onlineSession = onlineSession;
    }

    public BatchSessionRateLimit getBatchSession() {
        return batchSession;
    }

    public void setBatchSession(BatchSessionRateLimit batchSession) {
        this.batchSession = batchSession;
    }

    public InvoiceSendRateLimit getInvoiceSend() {
        return invoiceSend;
    }

    public void setInvoiceSend(InvoiceSendRateLimit invoiceSend) {
        this.invoiceSend = invoiceSend;
    }

    public InvoiceStatusRateLimit getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatusRateLimit invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public SessionListRateLimit getSessionList() {
        return sessionList;
    }

    public void setSessionList(SessionListRateLimit sessionList) {
        this.sessionList = sessionList;
    }

    public SessionInvoiceListRateLimit getSessionInvoiceList() {
        return sessionInvoiceList;
    }

    public void setSessionInvoiceList(SessionInvoiceListRateLimit sessionInvoiceList) {
        this.sessionInvoiceList = sessionInvoiceList;
    }

    public SessionMiscRateLimits getSessionMisc() {
        return sessionMisc;
    }

    public void setSessionMisc(SessionMiscRateLimits sessionMisc) {
        this.sessionMisc = sessionMisc;
    }

    public InvoiceMetadataRateLimit getInvoiceMetadata() {
        return invoiceMetadata;
    }

    public void setInvoiceMetadata(InvoiceMetadataRateLimit invoiceMetadata) {
        this.invoiceMetadata = invoiceMetadata;
    }

    public InvoiceExportRateLimit getInvoiceExport() {
        return invoiceExport;
    }

    public void setInvoiceExport(InvoiceExportRateLimit invoiceExport) {
        this.invoiceExport = invoiceExport;
    }

    public InvoiceExportStatusRateLimit getInvoiceStatusExport() {
        return invoiceStatusExport;
    }

    public void setInvoiceStatusExport(InvoiceExportStatusRateLimit invoiceStatusExport) {
        this.invoiceStatusExport = invoiceStatusExport;
    }

    public InvoiceDownloadRateLimit getInvoiceDownload() {
        return invoiceDownload;
    }

    public void setInvoiceDownload(InvoiceDownloadRateLimit invoiceDownload) {
        this.invoiceDownload = invoiceDownload;
    }

    public OtherRateLimit getOther() {
        return other;
    }

    public void setOther(OtherRateLimit other) {
        this.other = other;
    }

    public CollectiveIdentifierRateLimit getCollectiveIdentifier() {
        return collectiveIdentifier;
    }

    public void setCollectiveIdentifier(CollectiveIdentifierRateLimit collectiveIdentifier) {
        this.collectiveIdentifier = collectiveIdentifier;
    }
}
