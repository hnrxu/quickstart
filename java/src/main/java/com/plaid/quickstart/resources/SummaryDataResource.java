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
        return new SummaryDataResponse(r1, r2);

    }
    private static class SummaryDataResponse {
        @JsonProperty("most_frequent_categories")
        private final List<Category> sortedCategoriesFreq;


        @JsonProperty("most_spent_categories")
        private final List<Category> sortedCategoriesAmount;


        public SummaryDataResponse(List<Category> sortedCategoriesFreq, List<Category> sortedCategoriesAmount) {
            this.sortedCategoriesFreq = sortedCategoriesFreq;
            this.sortedCategoriesAmount = sortedCategoriesAmount;
        }
    }



    
}
