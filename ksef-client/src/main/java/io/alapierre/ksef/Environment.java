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
    DEMO("https://api-demo.ksef.mf.gov.pl"),
    PROD("https://api.ksef.mf.gov.pl"),
    TEST("https://api-test.ksef.mf.gov.pl");

    private final String url;
}
