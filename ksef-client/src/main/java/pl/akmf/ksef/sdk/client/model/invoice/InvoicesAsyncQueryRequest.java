package pl.akmf.ksef.sdk.client.model.invoice;

import pl.akmf.ksef.sdk.client.model.session.EncryptionInfo;

import java.util.List;

public class InvoicesAsyncQueryRequest {
  private EncryptionInfo encryption;

  private InvoiceQuerySubjectType subjectType;

  private InvoiceQueryDateRange dateRange;

  private String ksefNumber;

  private String invoiceNumber;

  private InvoiceQueryAmount amount;

  private InvoiceQuerySeller seller;

  private InvoiceQueryBuyer buyer ;

  private List<CurrencyCode> currencyCodes;

  private InvoicingMode invoicingMode ;

  private Boolean isSelfInvoicing;

  private InvoiceQuerySchemaType schemaType;

  private List<InvoiceMetadataInvoiceType> invoiceTypes;

  private Boolean hasAttachment;

  public InvoicesAsyncQueryRequest() {
  }



  public EncryptionInfo getEncryption() {
    return encryption;
  }

  public void setEncryption(EncryptionInfo encryption) {
    this.encryption = encryption;
  }

  public InvoiceQuerySubjectType getSubjectType() {
    return subjectType;
  }

  public void setSubjectType(InvoiceQuerySubjectType subjectType) {
    this.subjectType = subjectType;
  }

  public InvoiceQueryDateRange getDateRange() {
    return dateRange;
  }

  public void setDateRange(InvoiceQueryDateRange dateRange) {
    this.dateRange = dateRange;
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

  public InvoiceQueryAmount getAmount() {
    return amount;
  }

  public void setAmount(InvoiceQueryAmount amount) {
    this.amount = amount;
  }

  public InvoiceQuerySeller getSeller() {
    return seller;
  }

  public void setSeller(InvoiceQuerySeller seller) {
    this.seller = seller;
  }

  public InvoiceQueryBuyer getBuyer() {
    return buyer;
  }

  public void setBuyer(InvoiceQueryBuyer buyer) {
    this.buyer = buyer;
  }

  public List<CurrencyCode> getCurrencyCodes() {
    return currencyCodes;
  }

  public void setCurrencyCodes(List<CurrencyCode> currencyCodes) {
    this.currencyCodes = currencyCodes;
  }

  public InvoicingMode getInvoicingMode() {
    return invoicingMode;
  }

  public void setInvoicingMode(InvoicingMode invoicingMode) {
    this.invoicingMode = invoicingMode;
  }

  public Boolean getSelfInvoicing() {
    return isSelfInvoicing;
  }

  public void setSelfInvoicing(Boolean selfInvoicing) {
    isSelfInvoicing = selfInvoicing;
  }

  public InvoiceQuerySchemaType getSchemaType() {
    return schemaType;
  }

  public void setSchemaType(InvoiceQuerySchemaType schemaType) {
    this.schemaType = schemaType;
  }

  public List<InvoiceMetadataInvoiceType> getInvoiceTypes() {
    return invoiceTypes;
  }

  public void setInvoiceTypes(List<InvoiceMetadataInvoiceType> invoiceTypes) {
    this.invoiceTypes = invoiceTypes;
  }

  public Boolean getHasAttachment() {
    return hasAttachment;
  }

  public void setHasAttachment(Boolean hasAttachment) {
    this.hasAttachment = hasAttachment;
  }
}

