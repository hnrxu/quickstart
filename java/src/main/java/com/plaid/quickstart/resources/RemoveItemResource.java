package com.plaid.quickstart.resources;

import java.io.IOException;
import java.util.Arrays;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.plaid.client.model.ItemPublicTokenExchangeRequest;
import com.plaid.client.model.ItemPublicTokenExchangeResponse;
import com.plaid.client.model.ItemRemoveRequest;
import com.plaid.client.model.ItemRemoveResponse;
import com.plaid.client.request.PlaidApi;
import com.plaid.quickstart.QuickstartApplication;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/removeitem")
@Produces(MediaType.APPLICATION_JSON)
public class RemoveItemResource {

    private final PlaidApi plaidClient;

    public RemoveItemResource(PlaidApi plaidClient) {
        this.plaidClient = plaidClient;
    }

    @POST
    public Response removeItem() {
        if (QuickstartApplication.accessToken == null) {
            return Response.status(400).entity("{\"error\":\"No item to remove\"}").build();
        }

        ItemRemoveRequest request = new ItemRemoveRequest()
            .accessToken(QuickstartApplication.accessToken);

        try {
            retrofit2.Response<ItemRemoveResponse> response =
                plaidClient.itemRemove(request).execute();

            if (!response.isSuccessful()) {
                return Response.status(502)
                    .entity("{\"error\":\"Plaid itemRemove failed\"}")
                    .build();
            }

            TokenStore.deleteToken();

            QuickstartApplication.accessToken = null;
            return Response.ok("{\"ok\":true}").build();

        } catch (Exception e) {
            return Response.status(500)
                .entity("{\"error\":\"Server error removing item\"}")
                .build();
        }
    }
}
