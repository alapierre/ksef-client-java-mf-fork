package pl.akmf.ksef.sdk.client.model.permission.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EntityRoleType {

  COURTBAILIFF("CourtBailiff"),
  ENFORCEMENTAUTHORITY("EnforcementAuthority"),
  LOCALGOVERNMENTUNIT("LocalGovernmentUnit"),
  LOCALGOVERNMENTSUBUNIT("LocalGovernmentSubUnit"),
  VATGROUPUNIT("VatGroupUnit"),
  VATGROUPSUBUNIT("VatGroupSubUnit");

  private final String value;

  EntityRoleType(String value) {
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
  public static EntityRoleType fromValue(String value) {
    for (EntityRoleType b : EntityRoleType.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}

