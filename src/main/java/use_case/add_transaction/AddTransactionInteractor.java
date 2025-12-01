package use_case.add_transaction;

import entity.Transaction;
import entity.Label;
import java.util.*;

public class AddTransactionInteractor implements AddTransactionInputBoundary {
    private final AddTransactionOutputBoundary presenter;
    private final TransactionDataAccessInterface dataAccess;

    public AddTransactionInteractor(AddTransactionOutputBoundary presenter, TransactionDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(AddTransactionRequestModel requestModel) {
        float amount = requestModel.getAmount();
        String type = requestModel.getType();
        String note = requestModel.getNote();
        Date date = requestModel.getDate();

        if (amount <= 0) {
            presenter.prepareFailureView("the amount must be greater than zero\n");
            return;
        }
        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            presenter.prepareFailureView("only Income or Expense is supported");
            return;
        }

        // Generate unique ID based on existing transactions
        long nextId = 1;
        List<Transaction> existingTransactions = dataAccess.getAll();
        for (Transaction existing : existingTransactions) {
            if (existing.getId() >= nextId) {
                nextId = existing.getId() + 1;
            }
        }

        // Create transaction with Uncategorized label by default
        List<Label> defaultLabels = new ArrayList<>();
        Label uncategorizedLabel = dataAccess.getUncategorizedLabel();
        if (uncategorizedLabel != null) {
            defaultLabels.add(uncategorizedLabel);
        }

        // Create transaction
        Transaction t = new Transaction(
                nextId,
                amount,
                defaultLabels,
                note,
                date,
                type);

        // Save transaction
        dataAccess.save(t);

        AddTransactionResponseModel responseModel = new AddTransactionResponseModel(
                nextId,
                amount,
                type,
                note,
                t.getDate().toString());

        presenter.prepareSuccessView(responseModel);
    }
}