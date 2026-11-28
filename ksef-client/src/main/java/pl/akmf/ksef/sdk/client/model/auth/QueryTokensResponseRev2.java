package pl.akmf.ksef.sdk.client.model.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryTokensResponseRev2 {
    private String continuationToken;
    private List<AuthenticationTokenRev2> tokens;
}
