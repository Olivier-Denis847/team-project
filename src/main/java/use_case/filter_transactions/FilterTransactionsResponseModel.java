package use_case.filter_transactions;

import entity.Transaction;
import java.util.List;

public class FilterTransactionsResponseModel {
    private final List<Transaction> filtered;

    public FilterTransactionsResponseModel(List<Transaction> filtered) {
        this.filtered = filtered;
    }

    public List<Transaction> getFiltered() {
        return filtered;
    }
}

