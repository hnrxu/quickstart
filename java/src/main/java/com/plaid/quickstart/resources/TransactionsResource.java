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
  int target = 100;          // what you consider “ready”
  int maxAttempts = 6;      // don’t hang forever
  int attempt = 0;

  List<Transaction> finalTransactions = new ArrayList<>();

  while (attempt < maxAttempts) {
    String cursor = null;   // fresh full sync each attempt

    List<Transaction> added = new ArrayList<>();
    List<Transaction> modified = new ArrayList<>();
    List<RemovedTransaction> removed = new ArrayList<>();
    boolean hasMore = true;

    while (hasMore) {
      TransactionsSyncRequest request = new TransactionsSyncRequest()
          .accessToken(QuickstartApplication.accessToken)
          .cursor(cursor);

      Response<TransactionsSyncResponse> response = plaidClient.transactionsSync(request).execute();
      TransactionsSyncResponse responseBody = response.body();
      if (responseBody == null) throw new IOException("Null Plaid response body");

      cursor = responseBody.getNextCursor();

      added.addAll(responseBody.getAdded());
      modified.addAll(responseBody.getModified());
      removed.addAll(responseBody.getRemoved());
      hasMore = Boolean.TRUE.equals(responseBody.getHasMore());
    }

    // If we have enough, return immediately
    if (added.size() >= target) {
      added.sort(new CompareTransactionDate());
      return new TransactionsResponse(added);
    }

    // Not enough yet — wait briefly and try again
    attempt++;
    finalTransactions = added;
    Thread.sleep(1000);
  }

  // If still not enough, return whatever we have on the final attempt
  return new TransactionsResponse(finalTransactions);
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
