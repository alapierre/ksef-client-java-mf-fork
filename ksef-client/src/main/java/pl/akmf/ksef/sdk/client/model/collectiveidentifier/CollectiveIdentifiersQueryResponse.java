package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CollectiveIdentifiersQueryResponse {
    private String continuationToken;
    private List<CollectiveIdentifiersQueryResponseItem> collectiveIdentifiers = new ArrayList<>();

    public CollectiveIdentifiersQueryResponse() {
    }

    public CollectiveIdentifiersQueryResponse(String continuationToken, List<CollectiveIdentifiersQueryResponseItem> collectiveIdentifiers) {
        this.continuationToken = continuationToken;
        this.collectiveIdentifiers = collectiveIdentifiers;
    }

}
