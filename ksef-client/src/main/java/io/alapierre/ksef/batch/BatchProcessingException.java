package io.alapierre.ksef.batch;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 27.11.2025
 */
public class BatchProcessingException extends RuntimeException {

    public BatchProcessingException(String message) {
        super(message);
    }

    public BatchProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
