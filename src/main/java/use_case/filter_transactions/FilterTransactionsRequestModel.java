package use_case.filter_transactions;

import entity.Category;


public class FilterTransactionsRequestModel {
    private final String categoryInput;

    public FilterTransactionsRequestModel(String categoryInput) {
        this.categoryInput = categoryInput;
    }

    public String getCategoryInput() {
        return categoryInput;
    }
}

