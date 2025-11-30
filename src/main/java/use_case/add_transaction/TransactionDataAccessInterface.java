package use_case.add_transaction;

import entity.Transaction;
import java.util.List;

public interface TransactionDataAccessInterface {
    void save(Transaction transaction);

    List<Transaction> getAll();

    List<Transaction> getTransactions();
}
