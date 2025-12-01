package interface_adapter.filter;

import use_case.filter_transactions.FilterTransactionsOutputBoundary;
import use_case.filter_transactions.FilterTransactionsResponseModel;

public class FilterTransactionsPresenter implements FilterTransactionsOutputBoundary {
    private final FilterTransactionsViewModel viewModel;

    public FilterTransactionsPresenter(FilterTransactionsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(FilterTransactionsResponseModel responseModel) {
        viewModel.setFilteredTransactions(responseModel.getFiltered());
    }
}
