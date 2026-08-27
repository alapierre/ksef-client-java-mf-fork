package pl.akmf.ksef.sdk.client.model.collectiveidentifier;

import java.util.ArrayList;
import java.util.List;

public class CollectiveIdentifierInvoicesQueryRequest {
    private List<String> collectiveIdentifierNumbers = new ArrayList<>();

    public CollectiveIdentifierInvoicesQueryRequest() {
    }

    public CollectiveIdentifierInvoicesQueryRequest(List<String> collectiveIdentifierNumbers) {
        this.collectiveIdentifierNumbers = collectiveIdentifierNumbers;
    }

    public List<String> getCollectiveIdentifierNumbers() {
        return collectiveIdentifierNumbers;
    }

    public void setCollectiveIdentifierNumbers(List<String> collectiveIdentifierNumbers) {
        this.collectiveIdentifierNumbers = collectiveIdentifierNumbers;
    }
}
