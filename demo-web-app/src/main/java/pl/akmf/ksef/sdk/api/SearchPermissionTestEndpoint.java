package pl.akmf.ksef.sdk.api;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.akmf.ksef.sdk.api.services.DefaultKsefClient;
import pl.akmf.ksef.sdk.client.model.ApiException;
import pl.akmf.ksef.sdk.client.model.permission.search.EntityAuthorizationPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.EuEntityPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.PersonPermissionsQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityAuthorizationPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEntityRolesResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryEuEntityPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QueryPersonPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QuerySubordinateEntityRolesResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.QuerySubunitPermissionsResponse;
import pl.akmf.ksef.sdk.client.model.permission.search.SubordinateEntityRolesQueryRequest;
import pl.akmf.ksef.sdk.client.model.permission.search.SubunitPermissionsQueryRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions/query")
public class SearchPermissionTestEndpoint {

    private final DefaultKsefClient ksefClient;

    // Pobranie listy uprawnień do pracy w KSeF nadanych osobom fizycznym
    @PostMapping("/persons/grants")
    public ResponseEntity<QueryPersonPermissionsResponse> searchGrantedPersonPermissions(
            @RequestParam int pageOffset,
            @RequestParam int pageSize,
            @RequestBody PersonPermissionsQueryRequest request,
            @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ResponseEntity.ok(ksefClient.searchGrantedPersonPermissions(request, pageOffset, pageSize, authToken));
    }

    // Pobranie listy uprawnień administratora podmiotu podrzędnego
    @PostMapping("/subunits/grants")
    public ResponseEntity<QuerySubunitPermissionsResponse> searchSubunitAdminPermissions(
            @RequestParam int pageOffset,
            @RequestParam int pageSize,
            @RequestBody SubunitPermissionsQueryRequest request,
            @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ResponseEntity.ok(ksefClient.searchSubunitAdminPermissions(request, pageOffset, pageSize, authToken));
    }

    // Pobranie listy uprawnień do obsługi faktur nadanych podmiotom
    @GetMapping("/entities/roles")
    public ResponseEntity<QueryEntityRolesResponse> searchEntityInvoiceRoles(
            @RequestParam int pageOffset,
            @RequestParam int pageSize,
            @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ResponseEntity.ok(ksefClient.searchEntityInvoiceRoles(pageOffset, pageSize, authToken));
    }

    // Pobranie listy uprawnień do obsługi faktur nadanych podmiotom podrzędnym
    @PostMapping("/subordinate-entities/roles")
    public ResponseEntity<QuerySubordinateEntityRolesResponse> searchSubordinateEntityInvoiceRoles(
            @RequestBody SubordinateEntityRolesQueryRequest request,
            @RequestParam int pageOffset,
            @RequestParam int pageSize,
            @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ResponseEntity.ok(ksefClient.searchSubordinateEntityInvoiceRoles(request, pageOffset, pageSize, authToken));
    }

    // Pobranie listy uprawnień o charakterze upoważnień nadanych podmiotom
    @PostMapping("/authorizations/grants")
    public ResponseEntity<QueryEntityAuthorizationPermissionsResponse> searchEntityAuthorizationGrants(
            @RequestParam int pageOffset,
            @RequestParam int pageSize,
            @RequestBody EntityAuthorizationPermissionsQueryRequest request,
            @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ResponseEntity.ok(ksefClient.searchEntityAuthorizationGrants(request, pageOffset, pageSize, authToken));
    }

    // Pobranie listy uprawnień nadanych podmiotom unijnym
    @PostMapping("/eu-entities/grants")
    public ResponseEntity<QueryEuEntityPermissionsResponse> searchGrantedEuEntityPermissions(
            @RequestParam int pageOffset,
            @RequestParam int pageSize,
            @RequestBody EuEntityPermissionsQueryRequest request,
            @RequestHeader(name = "Authorization") String authToken) throws ApiException {
        return ResponseEntity.ok(ksefClient.searchGrantedEuEntityPermissions(request, pageOffset, pageSize, authToken));
    }
}
