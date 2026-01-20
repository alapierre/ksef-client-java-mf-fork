package io.alapierre.ksef;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 25.09.2025
 */
@Getter
@RequiredArgsConstructor
public enum Environment {
    TEST(
            "https://api-test.ksef.mf.gov.pl",
            "https://qr-test.ksef.mf.gov.pl",
            "v2"
    ),
    DEMO(
            "https://api-demo.ksef.mf.gov.pl",
            "https://qr-demo.ksef.mf.gov.pl",
            "v2"
    ),
    PROD(
            "https://api.ksef.mf.gov.pl",
            "https://qr.ksef.mf.gov.pl",
            "v2"
    );

    private final String apiBaseUrl;
    private final String qrBaseUrl;
    private final String suffixUri;
}
