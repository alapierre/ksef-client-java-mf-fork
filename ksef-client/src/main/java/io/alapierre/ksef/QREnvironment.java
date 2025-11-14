package io.alapierre.ksef;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 14.11.2025
 */
@Getter
@RequiredArgsConstructor
public enum QREnvironment {
    DEMO("https://ksef-demo.mf.gov.pl/client-app"),
    PROD("https://ksef.mf.gov.pl/client-app"),
    TEST("https://ksef-test.mf.gov.pl/client-app");

    private final String url;
}
