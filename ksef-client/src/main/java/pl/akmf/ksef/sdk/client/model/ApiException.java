package pl.akmf.ksef.sdk.client.model;

import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;

public class ApiException extends Exception {
    private static final long serialVersionUID = -7789526261274021206L;
    private int code = 0;
    private HttpHeaders responseHeaders = null;
    private String responseBody = null;

    public ApiException() {
    }

    public ApiException(Throwable throwable) {
        super(throwable);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable throwable, int code, HttpHeaders responseHeaders, String responseBody) {
        super(message, throwable);
        this.code = code;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public ApiException(String message, int code, HttpHeaders responseHeaders, String responseBody) {
        this(message, null, code, responseHeaders, responseBody);
    }

    public ApiException(String message, Throwable throwable, int code, HttpHeaders responseHeaders) {
        this(message, throwable, code, responseHeaders, null);
    }

    public ApiException(int code, HttpHeaders responseHeaders, String responseBody) {
        this(null, null, code, responseHeaders, responseBody);
    }

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(int code, String message, HttpHeaders responseHeaders, String responseBody) {
        this(code, message);
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public int getCode() {
        return code;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public static ApiException getApiException(String operationId, byte[] body, int statusCode, HttpHeaders headers) {
        String responseBody = body == null ? null : new String(body, StandardCharsets.UTF_8);
        String message = formatExceptionMessage(operationId, statusCode, responseBody);
        return new ApiException(statusCode, message, headers, responseBody);
    }

    private static String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }
}
