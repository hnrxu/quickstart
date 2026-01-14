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
        CategoryResponse r1 = new CategoryResponse(log.getMostFrequentCategory());
        CategoryResponse r2 = new CategoryResponse(log.getMostSpentCategory());
        return new SummaryDataResponse(r1, r2);

    }
    private static class SummaryDataResponse {
        @JsonProperty("most_frequent_category")
        private final CategoryResponse mostFrequentCategory;

        @JsonProperty("most_spent_category")
        private final CategoryResponse mostSpentCategory;

        public SummaryDataResponse(CategoryResponse mostFrequentCategory, CategoryResponse mostSpentCategory) {
            this.mostFrequentCategory = mostFrequentCategory;
            this.mostSpentCategory = mostSpentCategory;
        }
    }

    private static class CategoryResponse {
        @JsonProperty
        private final String name;

        @JsonProperty("total_spent")
        private final Double totalSpent;

         @JsonProperty("num_purchases")
        private final int numPurchases;

        public CategoryResponse(Category category) {
            this.name = category.getName();
            this.totalSpent = category.getTotalSpent();
            this.numPurchases = category.getNumPurchases();
        }
    }


    
}
