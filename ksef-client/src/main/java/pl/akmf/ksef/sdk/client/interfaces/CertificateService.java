package pl.akmf.ksef.sdk.client.interfaces;

import org.bouncycastle.asn1.x500.X500Name;
import pl.akmf.ksef.sdk.client.model.certificate.SelfSignedCertificate;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public interface CertificateService {

    String getSha256Fingerprint(X509Certificate certificate) throws CertificateEncodingException, NoSuchAlgorithmException;

    SelfSignedCertificate getPersonalCertificate(String givenName, String surname, String serialNumberPrefix,
                                                 String serialNumber, String commonName);

    SelfSignedCertificate getCompanySeal(String organizationName, String organizationIdentifier, String commonName);

    SelfSignedCertificate generateSelfSignedCertificateRsa(X500Name x500Name);

    SelfSignedCertificate generateSelfSignedCertificateEcdsa(X500Name x500Name);
}
