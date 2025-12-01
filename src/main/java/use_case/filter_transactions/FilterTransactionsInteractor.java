package use_case.filter_transactions;

import entity.Category;
import entity.Transaction;
import use_case.add_transaction.TransactionDataAccessInterface;

import java.util.List;
import java.util.stream.Collectors;

public class FilterTransactionsInteractor implements FilterTransactionsInputBoundary {

    private final TransactionDataAccessInterface dataAccess;
    private final FilterTransactionsOutputBoundary presenter;

    public FilterTransactionsInteractor(TransactionDataAccessInterface dataAccess,
                                        FilterTransactionsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void filterByCategory(FilterTransactionsRequestModel requestModel) {
        Category category;
        try {
            category = Category.valueOf(requestModel.getCategoryInput().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            presenter.present(new FilterTransactionsResponseModel(List.of()));
            return;
        }

        List<Transaction> filtered = dataAccess.getAll().stream()
                .filter(t -> t.getCategory() == category)
                .collect(Collectors.toList());

        presenter.present(new FilterTransactionsResponseModel(filtered));
    }
}

