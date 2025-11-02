package pl.akmf.ksef.sdk.sign;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KSPrivateKeyEntry;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class LocalSigningContext implements SignContextProvider {
    public static final String PKCS_12 = "PKCS12";
    public static final String CUSTOM_KEYSTORE_ALIAS = "alias";

    @Override
    public SignatureValue createSignatureValue(ToBeSigned toBeSigned, X509Certificate signatureCertificate, PrivateKey privateKey) {
        try {
            KeyStore keystore = KeyStore.getInstance(PKCS_12);
            keystore.load(null, null); // Initialize empty keystore
            Certificate[] chain = new Certificate[]{signatureCertificate};

            keystore.setKeyEntry(
                    CUSTOM_KEYSTORE_ALIAS,
                    privateKey,
                    null,
                    chain
            );

            // Convert keystore to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            keystore.store(baos, null);
            byte[] keystoreBytes = baos.toByteArray();

            DSSPrivateKeyEntry dssPrivateKeyEntry = getPrivateKeyEntry(keystore);
            try (Pkcs12SignatureToken token = createPkcs12Token(keystoreBytes)) {
                return token.sign(toBeSigned, DigestAlgorithm.SHA256, dssPrivateKeyEntry);
            }
        } catch (UnrecoverableEntryException | CertificateException | IOException | NoSuchAlgorithmException |
                 KeyStoreException e) {
            throw new SignSignatureException("Failed to create signature value for signing UPO", e);
        }
    }

    private DSSPrivateKeyEntry getPrivateKeyEntry(KeyStore keyStore) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        KeyStore.PasswordProtection keyPasswordProtection = new KeyStore.PasswordProtection(null);
        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(CUSTOM_KEYSTORE_ALIAS, keyPasswordProtection);
        return new KSPrivateKeyEntry(CUSTOM_KEYSTORE_ALIAS, privateKeyEntry);
    }

    private Pkcs12SignatureToken createPkcs12Token(byte[] keystoreBytes) {
        return new Pkcs12SignatureToken(keystoreBytes, new KeyStore.PasswordProtection(null));
    }
}
