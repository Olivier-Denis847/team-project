package data_access;

import entity.Transaction;
import use_case.add_transaction.TransactionDataAccessInterface;


import java.util.ArrayList;
import java.util.List;

public class InMemoryTransactionDataAccessObject implements TransactionDataAccessInterface {
    private final List<Transaction> transactions = new ArrayList<>();


    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAll() {
        return List.of();
    }


    @Override
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
