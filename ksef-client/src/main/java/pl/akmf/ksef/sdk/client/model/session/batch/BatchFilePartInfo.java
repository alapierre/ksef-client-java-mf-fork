package pl.akmf.ksef.sdk.client.model.session.batch;

public class BatchFilePartInfo {
    private int ordinalNumber;
    @Deprecated(since = "planowane usunięcie: 2025-12-05")
    private String fileName;
    private long fileSize;
    private String fileHash;

    public BatchFilePartInfo() {

    }

    public BatchFilePartInfo(int ordinalNumber, long fileSize, String fileHash) {
        this.ordinalNumber = ordinalNumber;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
    }

    @Deprecated
    public BatchFilePartInfo(int ordinalNumber, String fileName, long fileSize, String fileHash) {
        this.ordinalNumber = ordinalNumber;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
    }

    public int getOrdinalNumber() {
        return ordinalNumber;
    }

    public void setOrdinalNumber(int ordinalNumber) {
        this.ordinalNumber = ordinalNumber;
    }

    @Deprecated
    public String getFileName() {
        return fileName;
    }

    @Deprecated
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
