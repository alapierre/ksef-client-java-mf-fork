package pl.akmf.ksef.sdk.sign;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertUtil {
    private CertUtil() {
    }

    private static final String RSA = "RSA";
    private static final String EC = "EC";

    public static boolean isMatchingEcdsaPair(X509Certificate cert, PrivateKey privateKey) {
        if (cert == null || privateKey == null) return false;
        if (!EC.equalsIgnoreCase(cert.getPublicKey().getAlgorithm())) return false;
        if (!EC.equalsIgnoreCase(privateKey.getAlgorithm())) return false;
        return true;
    }

    public static boolean isMatchingRsaPair(X509Certificate cert, PrivateKey privateKey) {
        if (cert == null || privateKey == null) return false;
        if (!RSA.equalsIgnoreCase(cert.getPublicKey().getAlgorithm())) return false;
        if (!RSA.equalsIgnoreCase(privateKey.getAlgorithm())) return false;
        return true;
    }
}
