package pl.akmf.ksef.sdk.api;

import org.apache.commons.lang3.StringUtils;
import pl.akmf.ksef.sdk.client.model.ApiException;

import java.net.URLEncoder;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    private HttpUtils() {

    }

    public static String buildUrlWithParams(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?");

        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.isNotBlank(entry.getKey()) && StringUtils.isNotBlank(entry.getValue())) {

                if (!first) {
                    url.append("&");
                }

                url.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                url.append("=");
                url.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                first = false;

            }
        }
        return url.toString();
    }

    public static String buildUrlWithParams(String baseUrl, List<KeyValue> keyValues) {
        if (keyValues == null || keyValues.isEmpty()) {
            return baseUrl;
        }

        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?");

        boolean first = true;
        for (KeyValue keyValue : keyValues) {
            if (StringUtils.isNotBlank(keyValue.getKey()) && StringUtils.isNotBlank(keyValue.getValue())) {

                if (!first) {
                    url.append("&");
                }

                url.append(URLEncoder.encode(keyValue.getKey(), StandardCharsets.UTF_8));
                url.append("=");
                url.append(URLEncoder.encode(keyValue.getValue(), StandardCharsets.UTF_8));
                first = false;

            }
        }
        return url.toString();
    }

    public static void validateResponseStatus(String operationId,
                                              HttpResponse<byte[]> response,
                                              HttpStatus expectedStatus) throws ApiException {
        int statusCode = response.statusCode();

        if (expectedStatus.getCode() != statusCode) {
            String responseBody = response.body() == null ? null : new String(response.body(), StandardCharsets.UTF_8);
            String message = formatExceptionMessage(operationId, statusCode, responseBody);
            throw new ApiException(statusCode, message, response.headers(), responseBody);
        }
    }

    private static String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }

    static class KeyValue {
        private String key;
        private String value;

        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
