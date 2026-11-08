package pl.akmf.ksef.sdk.client.model.limit;

import io.alapierre.ksef.json.annotation.JsonName;
import lombok.Getter;
import lombok.Setter;

// Aktualnie obowiązujące limity ilości żądań przesyłanych do API.
@Getter
@Setter
public class EffectiveApiRateLimits {

    // Limity otwierania sesji interaktywnych.
    private OnlineSessionRateLimit onlineSession;
    // Limity zamykania sesji interaktywnych.
    private OnlineSessionRateLimit onlineSessionClose;
    // Limity otwierania sesji wsadowych.
    private BatchSessionRateLimit batchSession;
    // Limity zamykania sesji wsadowych.
    private BatchSessionRateLimit batchSessionClose;
    // Limity wysyłki faktur.
    private InvoiceSendRateLimit invoiceSend;
    // Limity pobierania statusu faktury z sesji.
    private InvoiceStatusRateLimit invoiceStatus;
    // Limity pobierania listy sesji.
    private SessionListRateLimit sessionList;
    // Limity pobierania listy faktur w sesji.
    private SessionInvoiceListRateLimit sessionInvoiceList;
    // Limity pozostałych operacji w ramach sesji.
    private SessionMiscRateLimits sessionMisc;
    // Limity pobierania metadanych faktur.
    private InvoiceMetadataRateLimit invoiceMetadata;
    // Limity eksportu paczki faktur.
    private InvoiceExportRateLimit invoiceExport;
    // Limity eksportu paczki faktur.
    @JsonName("invoiceExportStatus")
    private InvoiceExportStatusRateLimit invoiceStatusExport;
    // Limity pobierania faktur po numerze KSeF.
    private InvoiceDownloadRateLimit invoiceDownload;
    // Limity pozostałych operacji API.
    private OtherRateLimit other;
    // Limity generowania identyfikatorów zbiorczych.
    private CollectiveIdentifierRateLimit collectiveIdentifier;
    // Limity anonimowych operacji API.
    private AnonymousRateLimit anonymous;
    // Globalne limity API naliczane dla adresu IP.
    private GlobalRateLimit global;

    public EffectiveApiRateLimits() {
    }

    public EffectiveApiRateLimits(OnlineSessionRateLimit onlineSession, BatchSessionRateLimit batchSession,
                                  InvoiceSendRateLimit invoiceSend, InvoiceStatusRateLimit invoiceStatus,
                                  SessionListRateLimit sessionList, SessionInvoiceListRateLimit sessionInvoiceList,
                                  SessionMiscRateLimits sessionMisc, InvoiceMetadataRateLimit invoiceMetadata,
                                  InvoiceExportRateLimit invoiceExport, InvoiceExportStatusRateLimit invoiceStatusExport,
                                  InvoiceDownloadRateLimit invoiceDownload, OtherRateLimit other) {
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
    }

    public EffectiveApiRateLimits(OnlineSessionRateLimit onlineSession, BatchSessionRateLimit batchSession,
                                  InvoiceSendRateLimit invoiceSend, InvoiceStatusRateLimit invoiceStatus,
                                  SessionListRateLimit sessionList, SessionInvoiceListRateLimit sessionInvoiceList,
                                  SessionMiscRateLimits sessionMisc, InvoiceMetadataRateLimit invoiceMetadata,
                                  InvoiceExportRateLimit invoiceExport, InvoiceExportStatusRateLimit invoiceStatusExport,
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

    public EffectiveApiRateLimits(OnlineSessionRateLimit onlineSession, OnlineSessionRateLimit onlineSessionClose,
                                  BatchSessionRateLimit batchSession, BatchSessionRateLimit batchSessionClose,
                                  InvoiceSendRateLimit invoiceSend, InvoiceStatusRateLimit invoiceStatus,
                                  SessionListRateLimit sessionList, SessionInvoiceListRateLimit sessionInvoiceList,
                                  SessionMiscRateLimits sessionMisc, InvoiceMetadataRateLimit invoiceMetadata,
                                  InvoiceExportRateLimit invoiceExport, InvoiceExportStatusRateLimit invoiceStatusExport,
                                  InvoiceDownloadRateLimit invoiceDownload, OtherRateLimit other,
                                  CollectiveIdentifierRateLimit collectiveIdentifier, AnonymousRateLimit anonymous,
                                  GlobalRateLimit global) {
        this.onlineSession = onlineSession;
        this.onlineSessionClose = onlineSessionClose;
        this.batchSession = batchSession;
        this.batchSessionClose = batchSessionClose;
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
        this.anonymous = anonymous;
        this.global = global;
    }
}
