package com.plaid.quickstart.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.plaid.quickstart.persistence.Writable;

/**
 * Represents a list of immutable categories that will be attributed to
 * the user's stores/purchases. Allows the user to perform operations based
 * on category, such as most frequently purchased, highest-spending, and more.
 */
public class CategoryList implements Writable {
    // constants, may add or remove some categories later
    public static final Category GROCERIES = new Category("Groceries");
    public static final Category RENT = new Category("Rent");
    public static final Category UTILITIES = new Category("Utilities");
    public static final Category CLOTHING = new Category("Clothing");
    public static final Category DINING = new Category("Dining, drinks, entertainment");
    public static final Category GIFTS = new Category("Gifts");
    public static final Category VACATION = new Category("Vacation");
    public static final Category MISCELLANEOUS = new Category("Miscellaneous");


    private List<Category> categoryList;

    // EFFECTS: constructs categorylist with all constant categories
    public CategoryList() {
        categoryList = new ArrayList<Category>();
        // categoryList.add(GROCERIES);
        // categoryList.add(RENT);
        // categoryList.add(UTILITIES);
        // categoryList.add(CLOTHING);
        // categoryList.add(DINING);
        // categoryList.add(GIFTS);
        // categoryList.add(VACATION);
        // categoryList.add(MISCELLANEOUS);

    }

     // REQUIRES: store not already in list (maybe throw exception later)
    // MODIFIES: this
    // EFFECTS: adds a category to the store list
    public void addCategory(Category category) {
        if (!categoryList.contains(category)) {
            categoryList.add(category);
        }
    }

    
    // EFFECTS: returns the category with the highest num purchases. if multiple have
    // the same number, return last category 
    public Category getMostFrequentCategory() {
        Category mostFrequent = null;
        int mostPurchases = 0;
        List<Category> filteredCategoryList = new ArrayList<>();
        for (Category c: categoryList) {
            if (!c.getName().equals("TRANSFER_OUT")) {
                filteredCategoryList.add(c);
            }
        }

        for (Category c: filteredCategoryList) {
            if (c.getNumPurchases() >= mostPurchases) {
                mostFrequent = c;
                mostPurchases = c.getNumPurchases();
            }
        }
        return mostFrequent;
        

    }

    // EFFECTS: returns the category with the highest amt money spent. if multiple have
    // the same number, return last category (may change later)
    public Category getMostSpentCategory() {
        Category mostSpent = null;
        Double totalSpent = 0.0;
        List<Category> filteredCategoryList = new ArrayList<>();
        for (Category c: categoryList) {
            if (!c.getName().equals("TRANSFER_OUT")) {
                filteredCategoryList.add(c);
            }
        }

        for (Category c: filteredCategoryList) {
            if (c.getTotalSpent() >= totalSpent) {
                mostSpent = c;
                totalSpent = c.getTotalSpent();
            }
        }
        return mostSpent;
    }

    // MODIFIES: category
    // EFFECTS: resets number of purchases and total spent on each category to 0
    public void resetCategories() {
        for (Category c: categoryList) {
            c.resetCategory();
        }
    }


    // more summary methods will be added later

    // getters
    public List<Category> getAllCategories() {
        return categoryList;
    }

    // EFFECTS: returns a Category with the given name, or null if not found
    public Category getCategoryUsingName(String name) {
        for (Category c : categoryList) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<String> getAllCategoryNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Category c: categoryList) {
            names.add(c.getName());
        }
        return names;

    }


    // methods for saving 

    // EFFECTS: returns this as JSON object
    @Override
    public JSONObject toJson() {
        JSONObject jsonCL = new JSONObject();
        jsonCL.put("categoryList", categoriesToJson());

        return jsonCL;
        
    }

    // EFFECTS: returns categories in this categoryList as a JSON array
    private JSONArray categoriesToJson() {
        JSONArray categoryArray = new JSONArray();

        for (Category c : categoryList) {
            categoryArray.put(c.toJson());
        }

        return categoryArray;
    }


}
