package use_case.filter_transactions;

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

        String labelName = input.trim();

        // Filter by label name (case-insensitive)
        List<Transaction> filtered = dataAccess.getAll().stream()
                .filter(t -> t.getLabels() != null &&
                        t.getLabels().stream()
                                .anyMatch(label -> label.getLabelName().equalsIgnoreCase(labelName)))
                .collect(Collectors.toList());

        presenter.present(new FilterTransactionsResponseModel(filtered));
    }
}
