package pl.akmf.ksef.sdk.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.invoice.AsyncInvoicesQueryStatus;
import pl.akmf.ksef.sdk.client.model.invoice.DownloadInvoiceRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InitAsyncInvoicesQueryResponse;
import pl.akmf.ksef.sdk.client.model.invoice.InvoicesAsyncQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceMetadataQueryRequest;
import pl.akmf.ksef.sdk.client.model.invoice.QueryInvoiceMetadataResponse;

@RestController
@RequiredArgsConstructor
public class InvoicesController {
    private final DefaultKsefClient ksefClient;

    @GetMapping("/invoices/ksef/{ksefReferenceNumber}")
    public byte[] getInvoice(@PathVariable String ksefReferenceNumber,
                             @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.getInvoice(ksefReferenceNumber, authToken);
    }

    @PostMapping("/invoices/")
    public byte[] getInvoice(@RequestBody DownloadInvoiceRequest downloadInvoiceRequest,
                             @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.getInvoice(downloadInvoiceRequest, authToken);
    }

    @PostMapping("/invoices/metadata")
    QueryInvoiceMetadataResponse getInvoiceMetadata(@RequestParam Integer pageOffset,
                                                   @RequestParam Integer pageSize,
                                                   @RequestBody InvoiceMetadataQueryRequest request,
                                                   @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.queryInvoiceMetadata(pageOffset, pageSize, request, authToken);
    }

    @PostMapping("/invoices/query/async")
    InitAsyncInvoicesQueryResponse initAsyncInvoiceQuery(@RequestBody InvoicesAsyncQueryRequest request,
                                                         @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.initAsyncQueryInvoice(request, authToken);
    }

    @GetMapping("/invoices/query/async")
    AsyncInvoicesQueryStatus checkStatusAsyncQueryInvoice(@RequestParam String operationReferenceNumber,
                                                          @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.checkStatusAsyncQueryInvoice(operationReferenceNumber, authToken);
    }

}
