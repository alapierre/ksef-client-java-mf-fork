package pl.akmf.ksef.sdk.client.model.limit;

import io.alapierre.ksef.json.annotation.JsonName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRateLimitResponse {
    private OnlineSessionRateLimit onlineSession;
    private OnlineSessionRateLimit onlineSessionClose;
    private BatchSessionRateLimit batchSession;
    private BatchSessionRateLimit batchSessionClose;
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
    private AnonymousRateLimit anonymous;
    private GlobalRateLimit global;

    public GetRateLimitResponse() {
    }
}
