package io.alapierre.ksef.batch;

import io.alapierre.ksef.batch.model.InvoiceItem;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
@RequiredArgsConstructor
public class MockInvoiceSource implements InvoiceSource {

    private final String templatePath;
    private final String nip;
    private final int count;

    @Override
    public Iterator<InvoiceItem> iterator() {
        return new TemplateInvoiceIterator();
    }

    private class TemplateInvoiceIterator implements Iterator<InvoiceItem> {

        private final DateTimeFormatter DATE_FMT =
                DateTimeFormatter.ofPattern("yyyy-MM-dd");

        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < count;
        }

        @Override
        public InvoiceItem next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index++;

            String xmlTemplate = loadTemplate();

            String invoiceXml = xmlTemplate
                    .replace("#nip#", nip)
                    .replace("#invoicing_date#", LocalDate.now().format(DATE_FMT))
                    .replace("#invoice_number#", UUID.randomUUID().toString());

            byte[] content = invoiceXml.getBytes(StandardCharsets.UTF_8);
            byte[] hash = sha256(content);

            String fileName = "invoice-" + index + ".xml";

            return new InvoiceItem(
                    UUID.randomUUID().toString(),
                    fileName,
                    content,
                    Base64.getEncoder().encodeToString(hash)
            );
        }

        private String loadTemplate() {
            try {
                var in = Objects.requireNonNull(
                        MockInvoiceSource.class.getResourceAsStream(templatePath),
                        "Template not found on classpath: " + templatePath
                );
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException("Cannot read template: " + templatePath, e);
            }
        }

        private byte[] sha256(byte[] data) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                return digest.digest(data);
            } catch (Exception e) {
                throw new RuntimeException("Cannot compute SHA-256 hash", e);
            }
        }
    }

}
