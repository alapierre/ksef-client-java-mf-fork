package pl.akmf.ksef.sdk.client.model.permission.euentity;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum EuEntityPermissionType {
  
  INVOICE_WRITE("InvoiceWrite"),
  INVOICE_READ("InvoiceRead");

  @Getter(onMethod_ = @JsonValue)
  private final String value;

  EuEntityPermissionType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}

