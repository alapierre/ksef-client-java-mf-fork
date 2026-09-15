package pl.akmf.ksef.sdk.client.model.session.batch;

import java.util.regex.Pattern;

/** Shared validation of the OpenAPI BatchFileInfo and BatchFilePartInfo constraints. */
public final class BatchFileValidation {
    public static final long MAX_FILE_SIZE = 5_000_000_000L;
    public static final int MAX_PARTS = 50;
    private static final Pattern SHA256_BASE64 = Pattern.compile("^[A-Za-z0-9+/]{43}=$");

    private BatchFileValidation() {
    }

    public static void validateFileSize(long fileSize) {
        if (fileSize < 1 || fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Batch file size must be in range 1.." + MAX_FILE_SIZE + " bytes.");
        }
    }

    public static void validateFile(long fileSize, String fileHash) {
        validateFileSize(fileSize);
        validateHash(fileHash);
    }

    public static void validatePart(int ordinalNumber, long fileSize, String fileHash) {
        if (ordinalNumber < 1 || fileSize < 1) {
            throw new IllegalArgumentException("Batch part ordinal number and encrypted size must be at least 1.");
        }
        validateHash(fileHash);
    }

    public static void validatePartCount(long partCount) {
        if (partCount < 1 || partCount > MAX_PARTS) {
            throw new IllegalArgumentException("Batch file must contain 1.." + MAX_PARTS + " parts.");
        }
    }

    private static void validateHash(String fileHash) {
        if (fileHash == null || !SHA256_BASE64.matcher(fileHash).matches()) {
            throw new IllegalArgumentException("Batch file hash must be a SHA-256 hash encoded in Base64 (44 characters).");
        }
    }
}
