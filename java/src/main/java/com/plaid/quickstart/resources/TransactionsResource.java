package com.plaid.quickstart.resources;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import redis.clients.jedis.Jedis;

import retrofit2.Response;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
  private final PlaidApi plaidClient;
  private String savedCursor;
  private String tempCursor;


  public TransactionsResource(PlaidApi plaidClient) {
    this.plaidClient = plaidClient;
    this.tempCursor = null;
    this.savedCursor = null;
  }

    @GET
    public TransactionsResponse getTransactions() throws IOException, InterruptedException {
        int target = 100;          // what you consider “ready”
        int maxAttempts = 15;      // don’t hang forever
        int attempt = 0;

        if (QuickstartApplication.accessToken == null || QuickstartApplication.accessToken.isBlank()) {
                throw new javax.ws.rs.WebApplicationException("No access token set", 400);
        }
        if (QuickstartApplication.itemId == null || QuickstartApplication.itemId.isBlank()) {
            throw new javax.ws.rs.WebApplicationException("No itemId set", 400);
        }

        List<Transaction> addedTransactions = new ArrayList<>();
        List<Transaction> modifiedTransactions = new ArrayList<>();
        List<RemovedTransaction> removedTransactions = new ArrayList<>();

        String cursorKey = "savedCursor:" + QuickstartApplication.itemId;
        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) {
            savedCursor = jedis.get(cursorKey);
        }

        while (attempt < maxAttempts) {
            String cursor = savedCursor;   // fresh full sync each attempt

            List<Transaction> added = new ArrayList<>();
            List<Transaction> modified = new ArrayList<>();
            List<RemovedTransaction> removed = new ArrayList<>();
            boolean hasMore = true;

            while (hasMore) {
                TransactionsSyncRequest request = new TransactionsSyncRequest()
                    .accessToken(QuickstartApplication.accessToken)
                    .cursor(cursor);

                Response<TransactionsSyncResponse> response = plaidClient.transactionsSync(request).execute();
                if (!response.isSuccessful()) {
                    String err = response.errorBody() != null ? response.errorBody().string() : "(no errorBody)";
                    throw new IOException("Plaid /transactions/sync failed: HTTP " + response.code() + " " + err);
                }
                TransactionsSyncResponse responseBody = response.body();
                if (responseBody == null) throw new IOException("Null Plaid response body");

                cursor = responseBody.getNextCursor();

                added.addAll(responseBody.getAdded());
                modified.addAll(responseBody.getModified());
                removed.addAll(responseBody.getRemoved());
                hasMore = Boolean.TRUE.equals(responseBody.getHasMore());
            }

                // // If we have enough, return immediately
                // if (added.size() >= target) {
                // added.sort(new CompareTransactionDate());
                // return new TransactionsResponse(added);
                // }

                // Not enough yet — wait briefly and try again
                tempCursor = cursor;
                attempt++;
                addedTransactions = added;
                modifiedTransactions = modified;
                removedTransactions = removed;
                Thread.sleep(1000);
        }

        // store whatever we have on the final attempt
        //store in redis
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String jedisKey = "plaidTransactions:" + QuickstartApplication.itemId;
        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) {
            for (Transaction t: addedTransactions) {
                jedis.hset(jedisKey, t.getTransactionId(), mapper.writeValueAsString(t));
            }
            for (Transaction t: modifiedTransactions) {
                jedis.hset(jedisKey, t.getTransactionId(), mapper.writeValueAsString(t));
            }
            for (RemovedTransaction t: removedTransactions) {
                jedis.hdel(jedisKey, t.getTransactionId());
            }

        }

        List<Transaction> finalTransactions = new ArrayList<>();
        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) {
            for (String jsonTransaction: jedis.hvals(jedisKey)) {
                finalTransactions.add(mapper.readValue(jsonTransaction, Transaction.class));
            }
        }

        // store cursor in redis
        savedCursor = tempCursor;
        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) { 
            if (savedCursor != null) {
                jedis.set(cursorKey, savedCursor);
            }
        }



        finalTransactions.sort(new CompareTransactionDate()); // sort transactions
        PlaidReader plaidReader = new PlaidReader(finalTransactions);
        TransactionLogHost.getInstance().setLog(plaidReader.parseTransactionLog());

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
