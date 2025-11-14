package pl.akmf.ksef.sdk.api.services;

import io.alapierre.ksef.QREnvironment;
import pl.akmf.ksef.sdk.client.interfaces.VerificationLinkService;
import pl.akmf.ksef.sdk.client.model.qrcode.ContextIdentifierType;
import pl.akmf.ksef.sdk.system.SystemKSeFSDKException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class DefaultVerificationLinkService implements VerificationLinkService {

    private static final String SHA_256_WITH_RSA = "SHA256withRSA";
    private static final String SHA_256_WITH_ECDSA = "SHA256withECDSA";

    @Override
    public String buildInvoiceVerificationUrl(QREnvironment env, String nip, LocalDate issueDate, String invoiceHash) {
        String date = issueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        byte[] invoiceHashBytes = Base64.getDecoder().decode(invoiceHash);
        String invoiceHashUrlEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(invoiceHashBytes);

        return String.format("%s/invoice/%s/%s/%s", env.getUrl(), nip, date, invoiceHashUrlEncoded);
    }

    @Override
    public String buildCertificateVerificationUrl(QREnvironment env,
                                                  String sellerNip,
                                                  ContextIdentifierType contextIdentifierType,
                                                  String contextIdentifierValue,
                                                  String certificateSerial,
                                                  String invoiceHash,
                                                  PrivateKey privateKey) {
        byte[] invoiceHashBytes = Base64.getDecoder().decode(invoiceHash);
        String invoiceHashUrlEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(invoiceHashBytes);

        String pathToSign = String.format("%s/certificate/%s/%s/%s/%s/%s/", env.getUrl(), contextIdentifierType, contextIdentifierValue, sellerNip,
                        certificateSerial, invoiceHashUrlEncoded)
                .replace("https://", "");
        String signedHash = computeUrlEncodedSignedHash(pathToSign, privateKey);

        return String.format("%s/certificate/%s/%s/%s/%s/%s/%s", env.getUrl(), contextIdentifierType, contextIdentifierValue, sellerNip,
                certificateSerial, invoiceHashUrlEncoded, signedHash);
    }

    private String computeUrlEncodedSignedHash(String pathToSign, PrivateKey privateKey) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] sha = sha256.digest(pathToSign.getBytes(StandardCharsets.UTF_8));


            Signature signature;
            if (privateKey instanceof RSAPrivateKey) {
                signature = Signature.getInstance(SHA_256_WITH_RSA);
            } else if (privateKey instanceof ECPrivateKey) {
                signature = Signature.getInstance(SHA_256_WITH_ECDSA);
            } else {
                throw new SystemKSeFSDKException("Certificate not support RSA or ECDsa.", null);
            }

            signature.initSign(privateKey);
            signature.update(sha);
            byte[] signedBytes = signature.sign();

            String base64 = Base64.getEncoder().encodeToString(signedBytes);
            return URLEncoder.encode(base64, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
            throw new SystemKSeFSDKException("Cannot compute signature", e);
        }
    }
}
