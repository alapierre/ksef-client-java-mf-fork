package pl.akmf.ksef.sdk.client.model.session;

public class OnlineSessionLimit {
    private int maxInvoiceSizeInMib;
    private int maxInvoiceWithAttachmentSizeInMib;
    private int maxInvoices;

    public OnlineSessionLimit() {

    }

    public OnlineSessionLimit(int maxInvoiceSizeInMib, int maxInvoiceWithAttachmentSizeInMib, int maxInvoices) {
        this.maxInvoiceSizeInMib = maxInvoiceSizeInMib;
        this.maxInvoiceWithAttachmentSizeInMib = maxInvoiceWithAttachmentSizeInMib;
        this.maxInvoices = maxInvoices;
    }

    public int getMaxInvoiceSizeInMib() {
        return maxInvoiceSizeInMib;
    }

    public void setMaxInvoiceSizeInMib(int maxInvoiceSizeInMib) {
        this.maxInvoiceSizeInMib = maxInvoiceSizeInMib;
    }

    public int getMaxInvoiceWithAttachmentSizeInMib() {
        return maxInvoiceWithAttachmentSizeInMib;
    }

    public void setMaxInvoiceWithAttachmentSizeInMib(int maxInvoiceWithAttachmentSizeInMib) {
        this.maxInvoiceWithAttachmentSizeInMib = maxInvoiceWithAttachmentSizeInMib;
    }

    public int getMaxInvoices() {
        return maxInvoices;
    }

    public void setMaxInvoices(int maxInvoices) {
        this.maxInvoices = maxInvoices;
    }
}
