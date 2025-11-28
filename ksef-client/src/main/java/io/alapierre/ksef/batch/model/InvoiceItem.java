package io.alapierre.ksef.batch.model;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
public record InvoiceItem(
        String id,
        String fileName,
        byte[] content,
        String hash
) {
}
