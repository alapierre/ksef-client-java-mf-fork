package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CollectiveIdentifiersByKsefNumberQueryResponse {
    private String continuationToken;
    private List<CollectiveIdentifiersByKsefNumberQueryResponseItem> collectiveIdentifiers = new ArrayList<>();

    public CollectiveIdentifiersByKsefNumberQueryResponse() {
    }

    public CollectiveIdentifiersByKsefNumberQueryResponse(String continuationToken, List<CollectiveIdentifiersByKsefNumberQueryResponseItem> collectiveIdentifiers) {
        this.continuationToken = continuationToken;
        this.collectiveIdentifiers = collectiveIdentifiers;
    }

}
