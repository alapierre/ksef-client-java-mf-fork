package pl.akmf.ksef.sdk.client.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

/** Metadane zwracane przez listę tokenów oraz operację pobrania statusu w rev2. */
@Getter
@Setter
public class AuthenticationTokenRev2 {
    private String referenceNumber;
    private Identifier<AuthorType> authorIdentifier;
    private Identifier<ContextType> contextIdentifier;
    private String description;
    private List<Code<Permission>> requestedPermissions;
    private OffsetDateTime dateCreated;
    private OffsetDateTime lastUseDate;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private Code<Status> status;
    private Failure failure;

    /** Przy wartości FutureValue pole rawCode zawiera oryginalny kod zwrócony przez KSeF. */
    @Getter
    @Setter
    public static class Code<T extends Enum<T>> {
        private T code;
        private String rawCode;
        private String displayName;
    }

    @Getter
    @Setter
    public static class Identifier<T extends Enum<T>> {
        private Code<T> type;
        private String value;
    }

    @Getter
    @Setter
    public static class Failure {
        private String code;
        private String description;
        private List<String> details;
    }

    public enum AuthorType {
        Nip, Pesel, Fingerprint, FutureValue
    }

    public enum ContextType {
        Nip, InternalId, NipVatUe, PeppolId, FutureValue
    }

    public enum Permission {
        InvoiceRead, InvoiceWrite, CredentialsRead, CredentialsManage, SubunitManage,
        EnforcementOperations, Introspection, CollectiveIdentifierManage, FutureValue
    }

    public enum Status {
        Pending, Active, Revoking, Revoked, Failed, NotYetActive, AwaitingApproval, ApprovalExpired, Expired, FutureValue
    }
}
