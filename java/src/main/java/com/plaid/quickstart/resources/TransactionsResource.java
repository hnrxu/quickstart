package com.plaid.quickstart.resources;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plaid.client.request.PlaidApi;
import com.plaid.client.model.TransactionsSyncRequest;
import com.plaid.client.model.TransactionsSyncResponse;
import com.plaid.client.model.Transaction;
import com.plaid.client.model.TransactionsGetRequest;
import com.plaid.client.model.TransactionsGetRequestOptions;
import com.plaid.client.model.TransactionsGetResponse;
import com.plaid.client.model.RemovedTransaction;
import com.plaid.quickstart.QuickstartApplication;
import com.plaid.quickstart.TransactionLogHost;
import com.plaid.quickstart.model.TransactionLog;
import com.plaid.quickstart.persistence.JsonReaderPlaid;
import com.plaid.quickstart.persistence.PlaidReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import java.util.Map;

import retrofit2.Response;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
  private final PlaidApi plaidClient;


  public TransactionsResource(PlaidApi plaidClient) {
    this.plaidClient = plaidClient;
  }

    @GET
public TransactionsResponse getTransactions(@QueryParam("access_token") String token) throws IOException {
  String at = (token != null && !token.isBlank()) ? token : QuickstartApplication.accessToken;

  if (at == null || at.isBlank()) {
    throw new WebApplicationException(
      javax.ws.rs.core.Response.status(400)
        .entity(java.util.Map.of("error", "No access token available"))
        .build()
    );
  }

  // Plaid typically supports up to ~24 months of history via /transactions/get
  LocalDate endDate = LocalDate.now();
  LocalDate startDate = endDate.minusYears(2);

  List<Transaction> all = new ArrayList<>();
  int offset = 0;
  int count = 100; // max page size is usually 100

  while (true) {
    TransactionsGetRequestOptions options = new TransactionsGetRequestOptions()
      .count(count)
      .offset(offset);

    TransactionsGetRequest request = new TransactionsGetRequest()
      .accessToken(at)
      .startDate(startDate)
      .endDate(endDate)
      .options(options);

    retrofit2.Response<TransactionsGetResponse> response =
      plaidClient.transactionsGet(request).execute();

    if (!response.isSuccessful() || response.body() == null) {
      String err = response.errorBody() != null ? response.errorBody().string() : "(no error body)";
      throw new WebApplicationException(
        javax.ws.rs.core.Response.status(502)
          .entity(java.util.Map.of("error", "Plaid transactions/get failed", "status", response.code(), "details", err))
          .build()
      );
    }

    TransactionsGetResponse body = response.body();
    List<Transaction> page = body.getTransactions();
    all.addAll(page);

    int total = body.getTotalTransactions();
    offset += page.size();

    if (offset >= total || page.isEmpty()) break;
  }

  // Sort newest first
  all.sort((a, b) -> b.getDate().compareTo(a.getDate()));

  return new TransactionsResponse(all);
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
