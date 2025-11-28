package io.alapierre.ksef.batch.model;

import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
public record BatchResult(
        Path zipPath,
        long zipSize,
        String zipHash,
        List<BatchPartInfo> parts,
        List<InvoiceHash> invoiceHashes,

        byte[] iv,
        String encryptedCipherKey
) {

    public String encodedIv() {
        return Base64.getEncoder().encodeToString(iv);
    }

}
