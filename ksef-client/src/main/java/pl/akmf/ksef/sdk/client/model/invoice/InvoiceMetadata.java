package pl.akmf.ksef.sdk.client.model.invoice;

import pl.akmf.ksef.sdk.client.model.session.FormCode;

import java.time.OffsetDateTime;


/**
 * InvoiceMetadata
 */
public class InvoiceMetadata {
  private String ksefNumber;

  private String invoiceNumber;

  private OffsetDateTime issueDate;

  private OffsetDateTime acquisitionDate;

  private OffsetDateTime invoicingDate;

  private OffsetDateTime permanentStorageDate;

  private InvoiceMetadataSeller seller;

  private InvoiceMetadataBuyer buyer;

  private Double netAmount;

  private Double grossAmount;

  private Double vatAmount;

  private String currency;

  private InvoicingMode invoicingMode;

  private InvoiceMetadataInvoiceType invoiceType;

  private FormCode formCode;

  private Boolean isSelfInvoicing;

  private Boolean hasAttachment;

  public InvoiceMetadata() { 
  }

  public String getKsefNumber() {
    return ksefNumber;
  }

  public void setKsefNumber(String ksefNumber) {
    this.ksefNumber = ksefNumber;
  }

  public String getInvoiceNumber() {
    return invoiceNumber;
  }

  public void setInvoiceNumber(String invoiceNumber) {
    this.invoiceNumber = invoiceNumber;
  }

  public OffsetDateTime getIssueDate() {
    return issueDate;
  }

  public void setIssueDate(OffsetDateTime issueDate) {
    this.issueDate = issueDate;
  }

  public OffsetDateTime getAcquisitionDate() {
    return acquisitionDate;
  }

  public void setAcquisitionDate(OffsetDateTime acquisitionDate) {
    this.acquisitionDate = acquisitionDate;
  }

  public OffsetDateTime getInvoicingDate() {
    return invoicingDate;
  }

  public void setInvoicingDate(OffsetDateTime invoicingDate) {
    this.invoicingDate = invoicingDate;
  }

  public OffsetDateTime getPermanentStorageDate() {
    return permanentStorageDate;
  }

  public void setPermanentStorageDate(OffsetDateTime permanentStorageDate) {
    this.permanentStorageDate = permanentStorageDate;
  }

  public InvoiceMetadataSeller getSeller() {
    return seller;
  }

  public void setSeller(InvoiceMetadataSeller seller) {
    this.seller = seller;
  }

  public InvoiceMetadataBuyer getBuyer() {
    return buyer;
  }

  public void setBuyer(InvoiceMetadataBuyer buyer) {
    this.buyer = buyer;
  }

  public Double getNetAmount() {
    return netAmount;
  }

  public void setNetAmount(Double netAmount) {
    this.netAmount = netAmount;
  }

  public Double getGrossAmount() {
    return grossAmount;
  }

  public void setGrossAmount(Double grossAmount) {
    this.grossAmount = grossAmount;
  }

  public Double getVatAmount() {
    return vatAmount;
  }

  public void setVatAmount(Double vatAmount) {
    this.vatAmount = vatAmount;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public InvoicingMode getInvoicingMode() {
    return invoicingMode;
  }

  public void setInvoicingMode(InvoicingMode invoicingMode) {
    this.invoicingMode = invoicingMode;
  }

  public InvoiceMetadataInvoiceType getInvoiceType() {
    return invoiceType;
  }

  public void setInvoiceType(InvoiceMetadataInvoiceType invoiceType) {
    this.invoiceType = invoiceType;
  }

  public FormCode getFormCode() {
    return formCode;
  }

  public void setFormCode(FormCode formCode) {
    this.formCode = formCode;
  }

  public Boolean getSelfInvoicing() {
    return isSelfInvoicing;
  }

  public void setSelfInvoicing(Boolean selfInvoicing) {
    isSelfInvoicing = selfInvoicing;
  }

  public Boolean getHasAttachment() {
    return hasAttachment;
  }

  public void setHasAttachment(Boolean hasAttachment) {
    this.hasAttachment = hasAttachment;
  }
}

