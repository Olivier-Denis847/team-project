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
        String input = requestModel.getCategoryInput();

        if (input == null || input.trim().isEmpty()) {
            presenter.present(new FilterTransactionsResponseModel(List.of()));
            return;
        }

        String categoryName = input.trim();

        // Create a category object from the user input
        Category selectedCategory = new Category(categoryName);

        // Filter by category name (case-insensitive)
        List<Transaction> filtered = dataAccess.getAll().stream()
                .filter(t -> t.getCategory() != null &&
                        t.getCategory().getName().equalsIgnoreCase(selectedCategory.getName()))
                .collect(Collectors.toList());

        presenter.present(new FilterTransactionsResponseModel(filtered));
    }
}

