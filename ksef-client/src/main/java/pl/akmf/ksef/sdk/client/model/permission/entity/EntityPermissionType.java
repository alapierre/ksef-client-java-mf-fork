package pl.akmf.ksef.sdk.client.model.permission.entity;

import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum EntityPermissionType {
  
  INVOICE_WRITE("InvoiceWrite"),

  INVOICE_READ("InvoiceRead");

  @Getter(onMethod_ = @JsonValue)
  private final String value;

  EntityPermissionType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }
}

