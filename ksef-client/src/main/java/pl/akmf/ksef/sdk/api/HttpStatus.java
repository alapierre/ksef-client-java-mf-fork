package pl.akmf.ksef.sdk.api;

import java.util.Arrays;
import java.util.List;

public enum HttpStatus {

    CONTINUE(100),
    OK(200),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    FORBIDDEN(403);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static List<Integer> getErrorCodes() {
        return Arrays.asList(HttpStatus.BAD_REQUEST.getCode(), HttpStatus.UNAUTHORIZED.getCode(), HttpStatus.FORBIDDEN.getCode());
    }
}
