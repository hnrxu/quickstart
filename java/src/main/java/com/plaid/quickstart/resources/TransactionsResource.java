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


  public TransactionsResource(PlaidApi plaidClient) {
    this.plaidClient = plaidClient;
  }

  @GET
public TransactionsResponse getTransactions() throws IOException, InterruptedException {
  int maxAttempts = 6;
  int stableCountNeeded = 2;   // “same size” twice in a row
  int stableStreak = 0;

  Integer lastSize = null;
  List<Transaction> lastAdded = new ArrayList<>();

  for (int attempt = 0; attempt < maxAttempts; attempt++) {

    String cursor = null;
    List<Transaction> added = new ArrayList<>();
    boolean hasMore = true;

    while (hasMore) {
      TransactionsSyncRequest request = new TransactionsSyncRequest()
          .accessToken(QuickstartApplication.accessToken)
          .cursor(cursor);

      Response<TransactionsSyncResponse> response = plaidClient.transactionsSync(request).execute();
      TransactionsSyncResponse body = response.body();
      if (body == null) throw new IOException("Null Plaid response body");

      cursor = body.getNextCursor();
      added.addAll(body.getAdded());
      hasMore = Boolean.TRUE.equals(body.getHasMore());
    }

    lastAdded = added;

    // Check stability
    if (lastSize != null && added.size() == lastSize) {
      stableStreak++;
      if (stableStreak >= stableCountNeeded) break;
    } else {
      stableStreak = 0;
      lastSize = added.size();
    }

    // If still changing, wait briefly and try again
    Thread.sleep(800);
  }

  lastAdded.sort(new CompareTransactionDate());
  return new TransactionsResponse(lastAdded);
}



  private class CompareTransactionDate implements Comparator<Transaction> {
    @Override
    public int compare(Transaction o1, Transaction o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
  }

  
  private static class TransactionsResponse {
    @JsonProperty
    private final List<Transaction> latest_transactions;
  
    public TransactionsResponse(List<Transaction> latestTransactions) {
      this.latest_transactions = latestTransactions;
    }
  }
}
