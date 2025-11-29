package io.alapierre.ksef.qr.exceptions;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 29.11.2025
 */
public class VerificationLinkSiningException extends RuntimeException {
    public VerificationLinkSiningException(String message) {
        super(message);
    }

    public VerificationLinkSiningException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerificationLinkSiningException(Throwable cause) {
        super(cause);
    }
}
