package add_transaction;

import add_transaction.use_case.addtransaction.AddTransactionInteractor;
import add_transaction.use_case.addtransaction.AddTransactionOutputBoundary;
import add_transaction.use_case.addtransaction.AddTransactionRequestModel;
import add_transaction.use_case.addtransaction.AddTransactionResponseModel;
import add_transaction.use_case.addtransaction.TransactionDataAccessInterface;


import entity.Transaction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddTransactionInteractorTest {

    /**
     * A very small in-memory repository for testing.
     */
    private static class InMemoryTransactionRepository implements TransactionDataAccessInterface {

        private final ArrayList<Transaction> transactions = new ArrayList<>();
        private String lastSavedType = null;

        @Override
        public void save(Transaction transaction) {
            transactions.add(transaction);
            lastSavedType = transaction.getType();
        }

        @Override
        public ArrayList<Transaction> getAll() {
            return transactions;
        }

        @Override
        public List<Transaction> getTransactions() {
            return List.of();
        }
    }

    @Test
    void successTest() {

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                100,
                "Test note",
                "Income",
                new Date()
        );

        InMemoryTransactionRepository repo = new InMemoryTransactionRepository();

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                assertEquals(100, response.getAmount());
                assertEquals("Income", response.getType());
                assertEquals(1, repo.getAll().size()); // saved 1 transaction
                assertEquals("Income", repo.lastSavedType);
            }

            @Override
            public void prepareFailureView(String error) {
                fail("Unexpected failure: " + error);
            }
        };

        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, repo);
        interactor.execute(input);
    }

    @Test
    void failureAmountZeroTest() {

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                0,
                "Test note",
                "Expense",
                new Date()
        );

        InMemoryTransactionRepository repo = new InMemoryTransactionRepository();

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                fail("Use case should not succeed when amount is 0.");
            }

            @Override
            public void prepareFailureView(String error) {
                assertEquals("the amount must be greater than zero\n", error);
            }
        };

        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, repo);
        interactor.execute(input);
    }

    @Test
    void failureInvalidTypeTest() {

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                200,
                "Test note",
                "INVALID_TYPE",
                new Date()
        );

        InMemoryTransactionRepository repo = new InMemoryTransactionRepository();

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                fail("Use case should not succeed for invalid type.");
            }

            @Override
            public void prepareFailureView(String error) {
                assertEquals("only Income or Expense is supported", error);
            }
        };

        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, repo);
        interactor.execute(input);
    }
}
