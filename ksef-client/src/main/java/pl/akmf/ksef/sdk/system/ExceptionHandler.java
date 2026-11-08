package pl.akmf.ksef.sdk.system;

import io.alapierre.ksef.json.JsonSerializationException;
import io.alapierre.ksef.json.JsonSerializer;
import pl.akmf.ksef.sdk.api.HttpStatus;
import pl.akmf.ksef.sdk.api.Url;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.ExceptionResponse;
import pl.akmf.ksef.sdk.client.model.ForbiddenApiException;
import pl.akmf.ksef.sdk.client.model.ForbiddenProblemDetails;
import pl.akmf.ksef.sdk.client.model.KsefApiException;
import pl.akmf.ksef.sdk.client.model.TooManyRequestsResponse;
import pl.akmf.ksef.sdk.client.model.UnauthorizedApiException;
import pl.akmf.ksef.sdk.client.model.UnauthorizedProblemDetails;
import pl.akmf.ksef.sdk.client.model.exceptions.BadRequestApiException;
import pl.akmf.ksef.sdk.client.model.exceptions.BadRequestProblemDetails;
import pl.akmf.ksef.sdk.client.model.exceptions.GoneApiException;
import pl.akmf.ksef.sdk.client.model.exceptions.GoneProblemDetails;
import pl.akmf.ksef.sdk.client.model.exceptions.TooManyRequestsApiException;
import pl.akmf.ksef.sdk.client.model.exceptions.TooManyRequestsProblemDetails;

import java.io.IOException;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import java.util.Optional;

import static pl.akmf.ksef.sdk.api.HttpUtils.formatExceptionMessage;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_JSON;
import static pl.akmf.ksef.sdk.client.Headers.APPLICATION_PROBLEM_JSON;
import static pl.akmf.ksef.sdk.client.Headers.CONTENT_TYPE;
import static pl.akmf.ksef.sdk.client.Headers.RETRY_AFTER;

public class ExceptionHandler {

    private final JsonSerializer jsonSerializer;

    public ExceptionHandler(JsonSerializer jsonSerializer) {
        this.jsonSerializer = Objects.requireNonNull(jsonSerializer, "jsonSerializer");
    }

    public void handleException(HttpResponse<byte[]> response, Url operation) throws IOException, ApiException {
        ExceptionResponse exception = null;
        String uri = response.uri().toString();
        String method = Optional.of(response)
                .map(HttpResponse::request)
                .map(HttpRequest::method)
                .orElse("");

        String contentType = response.headers()
                .firstValue(CONTENT_TYPE)
                .orElse("")
                .toLowerCase();

        String message = formatExceptionMessage(operation.getOperationId(), response.statusCode(), response.body());

        if (response.body() != null && response.body().length > 0
                && (contentType.contains(APPLICATION_JSON)
                || contentType.contains(APPLICATION_PROBLEM_JSON))) {
            try {
                if (HttpStatus.UNAUTHORIZED.getCode() == response.statusCode()) {
                    tryParseException(() -> {
                        UnauthorizedProblemDetails unauthorizedProblemDetails = jsonSerializer.fromJson(response.body(), UnauthorizedProblemDetails.class);
                        if (unauthorizedProblemDetails == null || unauthorizedProblemDetails.getStatus() == 0 || unauthorizedProblemDetails.getTitle() == null) {
                            throw new IOException("fallback");
                        }
                        throw new UnauthorizedApiException(response.statusCode(), uri, method, message, response.headers(), unauthorizedProblemDetails);
                    });
                } else if (HttpStatus.FORBIDDEN.getCode() == response.statusCode()) {
                    tryParseException(() -> {
                        ForbiddenProblemDetails forbiddenProblemDetails = jsonSerializer.fromJson(response.body(), ForbiddenProblemDetails.class);
                        if (forbiddenProblemDetails == null || forbiddenProblemDetails.getStatus() == 0 || forbiddenProblemDetails.getTitle() == null) {
                            throw new IOException("fallback");
                        }
                        throw new ForbiddenApiException(response.statusCode(), uri, method, message, response.headers(), forbiddenProblemDetails);
                    });
                } else if (HttpStatus.GONE.getCode() == response.statusCode()) {
                    tryParseException(() -> {
                        GoneProblemDetails goneProblemDetails = jsonSerializer.fromJson(response.body(), GoneProblemDetails.class);
                        if (goneProblemDetails == null || goneProblemDetails.getStatus() == 0 || goneProblemDetails.getTitle() == null) {
                            throw new IOException("fallback");
                        }
                        throw new GoneApiException(response.statusCode(), uri, method, message, response.headers(), goneProblemDetails);
                    });
                } else if (HttpStatus.TOO_MANY_REQUESTS.getCode() == response.statusCode()) {
                    tryParseException(() -> {
                        TooManyRequestsProblemDetails tooManyRequestsProblemDetails = jsonSerializer.fromJson(response.body(), TooManyRequestsProblemDetails.class);
                        if (tooManyRequestsProblemDetails == null || tooManyRequestsProblemDetails.getStatus() == 0 || tooManyRequestsProblemDetails.getTitle() == null) {
                            throw new IOException("fallback");
                        }
                        throw new TooManyRequestsApiException(response.statusCode(), uri, method, message, response.headers(), tooManyRequestsProblemDetails);
                    });
                } else {
                    tryParseException(() -> {
                        BadRequestProblemDetails badRequestProblemDetails = jsonSerializer.fromJson(response.body(), BadRequestProblemDetails.class);
                        if (badRequestProblemDetails == null || badRequestProblemDetails.getStatus() == 0 || badRequestProblemDetails.getTitle() == null) {
                            throw new IOException("fallback");
                        }
                        throw new BadRequestApiException(response.statusCode(), uri, method, message, response.headers(), badRequestProblemDetails);
                    });
                }

                // Fallback do formatu legacy ApiErrorResponse
                exception = jsonSerializer.fromJson(response.body(), ExceptionResponse.class);
                if (HttpStatus.TOO_MANY_REQUESTS.getCode() == response.statusCode()
                        && exception != null && exception.getStatus() != null) {
                    setErrorDetailsFor429Status(response.headers(), exception.getStatus());
                }
            } catch (JsonSerializationException e) {
                throw new KsefApiException(response.statusCode(), uri, method, message, response.headers(), exception);
            }
        }
        throw new KsefApiException(response.statusCode(), uri, method, message, response.headers(), exception);
    }

    private static <T> void tryParseException(ErrorSupplier<T> supplier) throws ApiException {
        try {
            supplier.get();
        } catch (IOException ignore) {
        }
    }

    @FunctionalInterface
    interface ErrorSupplier<T> {
        T get() throws IOException, ApiException;
    }

    private void setErrorDetailsFor429Status(HttpHeaders headers, TooManyRequestsResponse status) {
        String retryAfterString = headers.firstValue(RETRY_AFTER)
                .orElse(null);

        if (retryAfterString != null && !retryAfterString.isBlank()) {
            Integer seconds = tryParseInt(retryAfterString);
            // Jeśli dostępne są sekundy z Retry-After, wykorzystanie ich wartości
            if (seconds != null) {
                status.setRetryAfterSeconds(seconds);
                status.setRecommendedDelay(Duration.ofSeconds(seconds));
                return;
            }
            OffsetDateTime retryAfterDate = tryParseOffsetDateTime(retryAfterString);
            // Jeśli dostępna jest data z Retry-After, obliczenie delty
            if (retryAfterDate != null) {
                status.setRetryAfterDate(retryAfterDate);
                Duration delta = Duration.between(OffsetDateTime.now(ZoneOffset.UTC), retryAfterDate);
                status.setRecommendedDelay(delta.isNegative() || delta.isZero()
                        ? Duration.ofSeconds(1)
                        : delta);
            }
        }
    }

    private static Integer tryParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static OffsetDateTime tryParseOffsetDateTime(String value) {
        try {
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
