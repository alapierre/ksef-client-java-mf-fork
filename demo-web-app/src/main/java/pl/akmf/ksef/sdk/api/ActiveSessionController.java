package pl.akmf.ksef.sdk.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.session.AuthenticationListResponse;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.session.SessionsQueryResponse;

@RestController
public class ActiveSessionController {

    DefaultKsefClient ksefClient;

    @PostMapping("/session/query/{pageSize}")
    public SessionsQueryResponse getSessions(@RequestBody SessionsQueryRequest request,
                                             @PathVariable Integer pageSize,
                                             @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.getSessions(request, pageSize, null, authToken);
    }

    @PostMapping("/session/active/{pageSize}")
    public AuthenticationListResponse getActiveSessions(@PathVariable Integer pageSize,
                                                        @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ksefClient.getActiveSessions(pageSize, null, authToken);
    }

    @DeleteMapping("/session/revoke/current")
    public void revokeCurrentSession(@RequestHeader(name = "Authorization") String authToken) throws ApiException {
        ksefClient.revokeCurrentSession(authToken);
    }

    @DeleteMapping("/session/revoke/{sessionReferenceNumber}")
    public void revokeSession(@PathVariable String sessionReferenceNumber,
                              @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        ksefClient.revokeSession(sessionReferenceNumber, authToken);
    }
}
