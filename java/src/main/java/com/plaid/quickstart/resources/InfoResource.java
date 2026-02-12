package com.plaid.quickstart.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.plaid.quickstart.QuickstartApplication;

import java.util.List;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

@Path("/info")
@Produces(MediaType.APPLICATION_JSON)
public class InfoResource {
  private final List<String> plaidProducts;

  public InfoResource(List<String> plaidProducts) {
    this.plaidProducts = plaidProducts;
  }

  public static class InfoResponse {
    @JsonProperty
    private final String itemId;
    @JsonProperty
    private final String accessToken;
    @JsonProperty
    private final List<String> products;
    @JsonProperty
    private final boolean hasAccessToken;


    public InfoResponse(List<String> plaidProducts, String accessToken, String itemId) {
      this.products = plaidProducts;
      this.accessToken = accessToken;
      this.itemId = itemId;
      this.hasAccessToken = accessToken != null && !accessToken.isBlank();
    }
  }

  @POST
  public InfoResponse getInfo() {
    String accessToken = null;
    String itemId = null;

    JSONObject userInfo = TokenStore.loadToken();
    if (userInfo != null) {
        if (userInfo.optString("accessToken", null) != null) {
            accessToken = userInfo.optString("accessToken", null);
        }
        if (userInfo.optString("itemId", null) != null) {
            itemId = userInfo.optString("itemId");
        }
    }
    return new InfoResponse(plaidProducts, accessToken,
        itemId);
  }
}
