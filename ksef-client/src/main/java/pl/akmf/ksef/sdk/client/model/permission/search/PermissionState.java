package pl.akmf.ksef.sdk.client.model.permission.search;

import io.alapierre.ksef.json.annotation.JsonCreator;
import io.alapierre.ksef.json.annotation.JsonValue;
import lombok.Getter;

public enum PermissionState {
  
  ACTIVE("Active"),
  
  INACTIVE("Inactive");

  @Getter(onMethod_ = @JsonValue)
  private final String value;

  PermissionState(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static PermissionState fromValue(String value) {
    for (PermissionState b : PermissionState.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

