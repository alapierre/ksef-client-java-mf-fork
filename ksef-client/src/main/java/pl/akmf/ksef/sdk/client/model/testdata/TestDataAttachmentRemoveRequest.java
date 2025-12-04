package pl.akmf.ksef.sdk.client.model.testdata;

import java.time.OffsetDateTime;

public class TestDataAttachmentRemoveRequest {
    private String nip;
    private OffsetDateTime expectedEndDate;

    public TestDataAttachmentRemoveRequest() {

    }

    public TestDataAttachmentRemoveRequest(String nip) {
        this.nip = nip;
    }

    public TestDataAttachmentRemoveRequest(String nip, OffsetDateTime expectedEndDate) {
        this.nip = nip;
        this.expectedEndDate = expectedEndDate;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public OffsetDateTime getExpectedEndDate() {
        return expectedEndDate;
    }

    public void setExpectedEndDate(OffsetDateTime expectedEndDate) {
        this.expectedEndDate = expectedEndDate;
    }
}
