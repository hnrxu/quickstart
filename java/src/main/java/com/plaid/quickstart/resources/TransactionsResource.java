package com.plaid.quickstart.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.model.TransactionsSyncRequest;
import com.plaid.client.model.TransactionsSyncResponse;
import com.plaid.client.model.Transaction;
import com.plaid.client.model.RemovedTransaction;
import com.plaid.quickstart.QuickstartApplication;
import com.plaid.quickstart.TransactionLogHost;
import com.plaid.quickstart.model.TransactionLog;
import com.plaid.quickstart.persistence.JsonReaderPlaid;
import com.plaid.quickstart.persistence.PlaidReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import retrofit2.Response;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
  private final PlaidApi plaidClient;

  // In-memory store (DEV ONLY). Key by itemId if you have it.
  private static String cursor = null;
  private static final java.util.Map<String, Transaction> store = new java.util.HashMap<>();

  public TransactionsResource(PlaidApi plaidClient) {
    this.plaidClient = plaidClient;
  }

  @GET
  public TransactionsResponse getTransactions() throws IOException {
    boolean hasMore = true;

    while (hasMore) {
      TransactionsSyncRequest request = new TransactionsSyncRequest()
          .accessToken(QuickstartApplication.accessToken)
          .cursor(cursor);

      Response<TransactionsSyncResponse> response = plaidClient.transactionsSync(request).execute();
      TransactionsSyncResponse body = response.body();
      if (body == null) throw new IOException("Null Plaid response body");

      // apply added
      for (Transaction t : body.getAdded()) {
        store.put(t.getTransactionId(), t);
      }

      // apply modified
      for (Transaction t : body.getModified()) {
        store.put(t.getTransactionId(), t);
      }

      // apply removed
      for (RemovedTransaction rt : body.getRemoved()) {
        store.remove(rt.getTransactionId());
      }

      cursor = body.getNextCursor();
      hasMore = Boolean.TRUE.equals(body.getHasMore());
    }

    // return FULL stable list
    List<Transaction> all = new ArrayList<>(store.values());
    all.sort((a, b) -> b.getDate().compareTo(a.getDate()));

    return new TransactionsResponse(all);
  }

  private static class TransactionsResponse {
    @JsonProperty
    private final List<Transaction> latest_transactions;

    public TransactionsResponse(List<Transaction> latestTransactions) {
      this.latest_transactions = latestTransactions;
    }
  }
}

