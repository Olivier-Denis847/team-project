package use_case.add_transaction;

import entity.Transaction;
import entity.Label;
import java.util.List;

public interface TransactionDataAccessInterface {
    void save(Transaction transaction);

    List<Transaction> getAll();

    List<Transaction> getTransactions();

    Label getUncategorizedLabel();
}