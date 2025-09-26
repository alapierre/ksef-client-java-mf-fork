package pl.akmf.ksef.sdk.client.model.invoice;

import pl.akmf.ksef.sdk.client.model.StatusInfo;


public class InitAsyncInvoicesQueryResponse {
    private String operationReferenceNumber;
    private StatusInfo status;

    public InitAsyncInvoicesQueryResponse() {
    }

    public String getOperationReferenceNumber() {
        return operationReferenceNumber;
    }

    public void setOperationReferenceNumber(String operationReferenceNumber) {
        this.operationReferenceNumber = operationReferenceNumber;
    }

    public StatusInfo getStatus() {
        return status;
    }

    public void setStatus(StatusInfo status) {
        this.status = status;
    }
}

