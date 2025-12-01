package interface_adapter.filter;

import entity.Transaction;
import java.util.List;

public class FilterTransactionsViewModel {
    private List<Transaction> filteredTransactions;

    public List<Transaction> getFilteredTransactions() { return filteredTransactions; }
    public void setFilteredTransactions(List<Transaction> filteredTransactions) {
        this.filteredTransactions = filteredTransactions;
    }
}
