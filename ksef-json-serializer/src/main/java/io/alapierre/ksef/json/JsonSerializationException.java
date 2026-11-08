package io.alapierre.ksef.json;

import java.io.IOException;

/** A JSON encoding or decoding failure, independent of the underlying library. */
public class JsonSerializationException extends IOException {
    private static final long serialVersionUID = 1L;

    public JsonSerializationException(String message) {
        super(message);
    }

    public JsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
