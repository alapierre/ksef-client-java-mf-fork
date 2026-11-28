package pl.akmf.ksef.sdk.client.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/** Okres ważności można pozostawić serwerowi KSeF. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KsefTokenRequestRev2 {
    private List<TokenPermissionType> permissions;
    private String description;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
}
