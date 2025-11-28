package io.alapierre.ksef.batch.model;

import java.nio.file.Path;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 26.11.2025
 */
public record BatchConfig(
        Path outputDir,
        long maxPartSize,
        boolean cleanup
) {
}
