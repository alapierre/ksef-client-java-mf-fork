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
    DEMO("https://ksef-demo.mf.gov.pl"),
    PROD("https://ksef.mf.gov.pl"),
    TEST("https://ksef-test.mf.gov.pl");

    private final String url;
}
