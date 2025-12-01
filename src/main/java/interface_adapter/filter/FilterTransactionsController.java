package interface_adapter.filter;

import use_case.filter_transactions.FilterTransactionsInputBoundary;
import use_case.filter_transactions.FilterTransactionsRequestModel;

public class FilterTransactionsController {
    private final FilterTransactionsInputBoundary interactor;

    public FilterTransactionsController(FilterTransactionsInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void filter(String category) {
        FilterTransactionsRequestModel request = new FilterTransactionsRequestModel(category);
        interactor.filterByCategory(request);
    }
}
