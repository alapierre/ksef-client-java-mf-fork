package pl.akmf.ksef.sdk.client.model.certificate;

import java.time.OffsetDateTime;

public class CertificateInfo {
    private String certificateSerialNumber;
    private String name;
    private CertificateType type;
    private String commonName;
    private CertificateListItemStatus status;
    private String subjectIdentifier;
    private String subjectIdentifierType;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private OffsetDateTime lastUseDate;

    public CertificateInfo() {

    }

    public CertificateInfo(String certificateSerialNumber, String name, CertificateType type,
                           String commonName, CertificateListItemStatus status, String subjectIdentifier,
                           String subjectIdentifierType, OffsetDateTime validFrom, OffsetDateTime validTo,
                           OffsetDateTime lastUseDate) {
        this.certificateSerialNumber = certificateSerialNumber;
        this.name = name;
        this.type = type;
        this.commonName = commonName;
        this.status = status;
        this.subjectIdentifier = subjectIdentifier;
        this.subjectIdentifierType = subjectIdentifierType;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.lastUseDate = lastUseDate;
    }

    public String getCertificateSerialNumber() {
        return certificateSerialNumber;
    }

    public void setCertificateSerialNumber(String certificateSerialNumber) {
        this.certificateSerialNumber = certificateSerialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public CertificateListItemStatus getStatus() {
        return status;
    }

    public void setStatus(CertificateListItemStatus status) {
        this.status = status;
    }

    public String getSubjectIdentifier() {
        return subjectIdentifier;
    }

    public void setSubjectIdentifier(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }

    public String getSubjectIdentifierType() {
        return subjectIdentifierType;
    }

    public void setSubjectIdentifierType(String subjectIdentifierType) {
        this.subjectIdentifierType = subjectIdentifierType;
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

    public OffsetDateTime getLastUseDate() {
        return lastUseDate;
    }

    public void setLastUseDate(OffsetDateTime lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    public CertificateType getType() {
        return type;
    }

    public void setType(CertificateType type) {
        this.type = type;
    }
}