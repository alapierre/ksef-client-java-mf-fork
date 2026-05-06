package pl.akmf.ksef.sdk.client.model.certificate.publickey;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


public class PublicKeyCertificate {
    private String certificate;
    private String certificateId;
    private String publicKeyId;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private List<PublicKeyCertificateUsage> usage = new ArrayList<>();

    public PublicKeyCertificate() {
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public OffsetDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(OffsetDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public OffsetDateTime getValidTo() {
        return validTo;
    }

    public void setValidTo(OffsetDateTime validTo) {
        this.validTo = validTo;
    }

    public List<PublicKeyCertificateUsage> getUsage() {
        return usage;
    }

    public void setUsage(List<PublicKeyCertificateUsage> usage) {
        this.usage = usage;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getPublicKeyId() {
        return publicKeyId;
    }

    public void setPublicKeyId(String publicKeyId) {
        this.publicKeyId = publicKeyId;
    }
}
