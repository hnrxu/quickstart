package com.plaid.quickstart.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.plaid.client.model.Transaction;
import com.plaid.quickstart.TransactionLogHost;
import com.plaid.quickstart.model.Category;
import com.plaid.quickstart.model.Store;
import com.plaid.quickstart.model.TransactionLog;

@Path("/summarydata")
@Produces(MediaType.APPLICATION_JSON)
public class SummaryDataResource {
    private TransactionLog log;


    public SummaryDataResource() {
        log = TransactionLogHost.getInstance().getLog();
    }

    @GET
    public SummaryDataResponse getData() {
        log = TransactionLogHost.getInstance().getLog();
        List<Category> r1 = log.getSortedFreqCategories();
        List<Category> r2 = log.getSortedAmountCategories();
        List<Store> r3 = log.getSortedFreqStores();
        List<Store> r4 = log.getSortedAmountStores();
        return new SummaryDataResponse(r1, r2, r3, r4);

    }
    private static class SummaryDataResponse {
        @JsonProperty("most_frequent_categories")
        private final List<Category> sortedCategoriesFreq;

        @JsonProperty("most_spent_categories")
        private final List<Category> sortedCategoriesAmount;

         @JsonProperty("most_frequent_stores")
        private final List<Store> sortedStoresFreq;

         @JsonProperty("most_spent_stores")
        private final List<Store> sortedStoresAmount;


        public SummaryDataResponse(List<Category> sortedCategoriesFreq, 
                                List<Category> sortedCategoriesAmount,
                                List<Store> sortedStoresFreq,
                                List<Store> sortedStoresAmount) {
            this.sortedCategoriesFreq = sortedCategoriesFreq;
            this.sortedCategoriesAmount = sortedCategoriesAmount;
            this.sortedStoresFreq = sortedStoresFreq;
            this.sortedStoresAmount = sortedStoresAmount;
        }
    }



    
}
