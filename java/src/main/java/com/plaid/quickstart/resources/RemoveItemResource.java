package com.plaid.quickstart.resources;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.ItemPublicTokenExchangeResponse;
import com.plaid.client.model.ItemRemoveRequest;
import com.plaid.client.model.ItemRemoveResponse;
import com.plaid.client.model.RemovedTransaction;
import com.plaid.client.model.Transaction;
import com.plaid.client.request.PlaidApi;
import com.plaid.quickstart.QuickstartApplication;

import redis.clients.jedis.Jedis;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;


@Path("/removeitem")
@Produces(MediaType.APPLICATION_JSON)
public class RemoveItemResource {

    private final PlaidApi plaidClient;

    public RemoveItemResource(PlaidApi plaidClient) {
        this.plaidClient = plaidClient;
    }

    @POST
    public Response removeItem() {
    // 1) Load from Redis (source of truth)
    JSONObject userInfo = TokenStore.loadToken();
    if (userInfo == null) {
        return Response.status(400).entity("{\"error\":\"No stored item\"}").build();
    }

    String accessToken = userInfo.optString("accessToken", null);
    String itemId = userInfo.optString("itemId", null);

    if (accessToken == null || accessToken.isBlank()) {
        return Response.status(400).entity("{\"error\":\"No access token\"}").build();
    }

    // 2) Call Plaid item/remove FIRST
    try {
        ItemRemoveRequest request = new ItemRemoveRequest().accessToken(accessToken);
        retrofit2.Response<ItemRemoveResponse> response = plaidClient.itemRemove(request).execute();

        if (!response.isSuccessful()) {
        // IMPORTANT: return Plaid error body so you can see why
        String body = response.errorBody() != null ? response.errorBody().string() : "";
        return Response.status(502).entity("{\"error\":\"Plaid itemRemove failed\",\"details\":" + JSONObject.quote(body) + "}").build();
        }

        // 3) Only after success: delete Redis + cached transaction keys
        TokenStore.deleteToken();

        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) {
        if (itemId != null) {
            jedis.del("plaidTransactions:" + itemId);
            jedis.del("savedCursor:" + itemId);
        }
        }

        // 4) Clear in-memory too (optional)
        QuickstartApplication.accessToken = null;
        QuickstartApplication.itemId = null;

        return Response.ok("{\"ok\":true}").build();

    } catch (Exception e) {
        return Response.status(500).entity("{\"error\":\"Server error removing item\"}").build();
    }
    }

}
