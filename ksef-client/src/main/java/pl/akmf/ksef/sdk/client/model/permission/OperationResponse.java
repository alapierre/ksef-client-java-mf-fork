package pl.akmf.ksef.sdk.client.model.permission;

import java.io.Serializable;

public class OperationResponse implements Serializable {
    private String operationReferenceNumber;

    public OperationResponse() {
    }

    public OperationResponse(String operationReferenceNumber) {
        this.operationReferenceNumber = operationReferenceNumber;
    }

    public String getOperationReferenceNumber() {
        return operationReferenceNumber;
    }

    public void setOperationReferenceNumber(String operationReferenceNumber) {
        this.operationReferenceNumber = operationReferenceNumber;
    }
}
