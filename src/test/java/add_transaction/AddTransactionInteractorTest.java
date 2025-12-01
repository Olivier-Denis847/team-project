package add_transaction;

import use_case.add_transaction.AddTransactionInteractor;
import use_case.add_transaction.AddTransactionOutputBoundary;
import use_case.add_transaction.AddTransactionRequestModel;
import use_case.add_transaction.AddTransactionResponseModel;
import use_case.add_transaction.TransactionDataAccessInterface;

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

        @Override
        public entity.Label getUncategorizedLabel() {
            return new entity.Label(0, "Uncategorized", "#CCCCCC", "Default label");
        }
    }

    @Test
    void successTest() {

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                100,
                "Test note",
                "Income",
                new Date());

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
                new Date());

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
                new Date());

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

    @Test
    void successExpenseTest() {

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                150,
                "Dinner",
                "Expense",
                new Date());

        InMemoryTransactionRepository repo = new InMemoryTransactionRepository();

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                assertEquals(150, response.getAmount());
                assertEquals("Expense", response.getType());
                assertEquals(1, repo.getAll().size());
                assertEquals("Expense", repo.lastSavedType);
            }

            @Override
            public void prepareFailureView(String error) {
                fail("Failure unexpected: " + error);
            }
        };

        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, repo);
        interactor.execute(input);
    }

    @Test
    void nextIdIncrementsCorrectlyTest() {

        InMemoryTransactionRepository repo = new InMemoryTransactionRepository();

        Transaction existing = new Transaction(
                5,
                300,
                new ArrayList<>(),
                "Prev",
                new Date(),
                "Income");
        repo.save(existing);

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                100,
                "New",
                "Income",
                new Date());

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                assertEquals(6, response.getTransactionId());
            }

            @Override
            public void prepareFailureView(String error) {
                fail("Should not fail: " + error);
            }
        };

        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, repo);
        interactor.execute(input);
    }

    @Test
    void successWithoutUncategorizedLabelTest() {

        TransactionDataAccessInterface repo = new TransactionDataAccessInterface() {

            private final ArrayList<Transaction> transactions = new ArrayList<>();

            @Override
            public void save(Transaction transaction) {
                transactions.add(transaction);
            }

            @Override
            public ArrayList<Transaction> getAll() {
                return transactions;
            }

            @Override
            public List<Transaction> getTransactions() {
                return List.of();
            }

            @Override
            public entity.Label getUncategorizedLabel() {
                return null;
            }
        };

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                100,
                "Note",
                "Income",
                new Date()
        );

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                assertEquals(100, response.getAmount());
                assertEquals("Income", response.getType());
                assertEquals(1, repo.getAll().size());
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
    void nextIdSkipsLowerExistingIdsTest() {

        InMemoryTransactionRepository repo = new InMemoryTransactionRepository();

        repo.save(new Transaction(
                5, 300, new ArrayList<>(), "Old", new Date(), "Income"
        ));

        repo.save(new Transaction(
                1, 100, new ArrayList<>(), "Lower", new Date(), "Income"
        ));

        AddTransactionRequestModel input = new AddTransactionRequestModel(
                200,
                "New",
                "Income",
                new Date()
        );

        AddTransactionOutputBoundary presenter = new AddTransactionOutputBoundary() {
            @Override
            public void prepareSuccessView(AddTransactionResponseModel response) {
                assertEquals(6, response.getTransactionId());
            }

            @Override
            public void prepareFailureView(String error) {
                fail("Unexpected failure: " + error);
            }
        };

        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, repo);
        interactor.execute(input);
    }


}

