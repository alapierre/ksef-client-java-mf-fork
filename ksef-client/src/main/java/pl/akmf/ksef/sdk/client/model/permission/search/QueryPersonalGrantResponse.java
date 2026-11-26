package pl.akmf.ksef.sdk.client.model.permission.search;

import java.util.List;

public class QueryPersonalGrantResponse {
    private List<QueryPersonalGrantItem> permissions;
    private List<OwnerPermissionScope> ownerPermissions;
    private Boolean hasMore;

    public QueryPersonalGrantResponse() {

    }

    public List<QueryPersonalGrantItem> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<QueryPersonalGrantItem> permissions) {
        this.permissions = permissions;
    }

    /** @return lista uprawnień właścicielskich lub null, jeśli KSeF jej nie udostępnił */
    public List<OwnerPermissionScope> getOwnerPermissions() {
        return ownerPermissions;
    }

    public void setOwnerPermissions(List<OwnerPermissionScope> ownerPermissions) {
        this.ownerPermissions = ownerPermissions;
    }

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }
}
