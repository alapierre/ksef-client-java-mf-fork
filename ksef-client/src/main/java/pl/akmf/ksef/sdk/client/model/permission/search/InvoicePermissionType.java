package pl.akmf.ksef.sdk.client.model.permission.search;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


public enum InvoicePermissionType {
  
  SELFINVOICING("SelfInvoicing"),
  TAXREPRESENTATIVE("TaxRepresentative"),
  RRINVOICING("RRInvoicing");

  private final String value;

  InvoicePermissionType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static InvoicePermissionType fromValue(String value) {
    for (InvoicePermissionType b : InvoicePermissionType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

