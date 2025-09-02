package pl.akmf.ksef.sdk;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TestUtils {

    public static String generateRandomNIP() {
        StringBuilder sb = new StringBuilder(10);
        Random random = new Random();

        sb.append("73");
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(0, 10));
        }

        return sb.toString();
    }

    public static String generateRandomVatEu() {
        StringBuilder sb = new StringBuilder(10);
        Random random = new Random();
        sb.append("DE");
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(0, 10));
        }

        return sb.toString();
    }
}
