package pl.akmf.ksef.sdk.client.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
public class QueryTokensFiltersRev2 {
    private List<Status> statuses;
    private String description;
    private String authorIdentifier;
    private AuthorTokenIdentifier.IdentifierType authorIdentifierType;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    public enum Status {
        Pending, Active, Revoking, Revoked, Failed, NotYetActive, AwaitingApproval, ApprovalExpired, Expired
    }
}
