package pl.akmf.ksef.sdk.api.builders.collectiveidentifier;

import pl.akmf.ksef.sdk.client.model.collectiveidentifier.CollectiveIdentifierInvoicesQueryRequest;

import java.util.ArrayList;
import java.util.List;

public class CollectiveIdentifierInvoicesQueryRequestBuilder {
    private List<String> collectiveIdentifierNumbers = new ArrayList<>();

    public CollectiveIdentifierInvoicesQueryRequestBuilder withCollectiveIdentifierNumbers(List<String> collectiveIdentifierNumbers) {
        this.collectiveIdentifierNumbers = collectiveIdentifierNumbers;
        return this;
    }

    public CollectiveIdentifierInvoicesQueryRequestBuilder addCollectiveIdentifierNumber(String collectiveIdentifierNumber) {
        if (this.collectiveIdentifierNumbers == null) {
            this.collectiveIdentifierNumbers = new ArrayList<>();
        }
        this.collectiveIdentifierNumbers.add(collectiveIdentifierNumber);
        return this;
    }

    public CollectiveIdentifierInvoicesQueryRequest build() {
        return new CollectiveIdentifierInvoicesQueryRequest(collectiveIdentifierNumbers);
    }
}
