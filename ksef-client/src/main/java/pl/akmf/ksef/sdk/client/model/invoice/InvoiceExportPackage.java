package pl.akmf.ksef.sdk.client.model.invoice;

import lombok.Getter;
import lombok.Setter;
import pl.akmf.ksef.sdk.client.model.session.batch.CompressionType;

import java.time.OffsetDateTime;
import java.util.List;

@Setter
@Getter
public class InvoiceExportPackage {
    // Liczba faktur w paczce.
    private int invoiceCount;

    // Rozmiar paczki w bajtach.
    private int size;

    // Lista części paczki do pobrania.
    private List<InvoicePackagePart> parts;

    // Czy paczka została obcięta (nie zawiera wszystkich faktur z zakresu).
    private Boolean isTruncated;

    // Data wystawienia ostatniej faktury w paczce.
    private OffsetDateTime lastIssueDate;

    // Data sprzedaży ostatniej faktury w paczce.
    private OffsetDateTime lastInvoicingDate;

    // Data trwałego przechowywania ostatniej faktury w paczce.
    private OffsetDateTime lastPermanentStorageDate;

    // Dotyczy wyłącznie zapytań filtrowanych po typie daty PermanentStorage. Jeśli zapytanie dotyczyło najnowszego okresu, wartość ta może być wartością nieznacznie skorygowaną względem górnej granicy podanej w warunkach zapytania.
    private OffsetDateTime permanentStorageHwmDate;

    private CompressionType compressionType;

    public InvoiceExportPackage() {

    }

}
