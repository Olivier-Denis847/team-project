package use_case.add_transaction;

import entity.Category;
import entity.Transaction;
import entity.Label;
import java.util.*;

public class AddTransactionInteractor implements AddTransactionInputBoundary {
    private final AddTransactionOutputBoundary presenter;
    private final TransactionDataAccessInterface dataAccess;
    private int nextId = 1;

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
        String categoryInput = requestModel.getCategoryInput();

        if (amount <= 0) {
            presenter.prepareFailureView("the amount must be greater than zero\n");
            return;
        }
        // delete this if branch later
        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            presenter.prepareFailureView("only Income or Expense is supported");
            return;
        }

        // Create transaction with Uncategorized label by default
        List<Label> defaultLabels = new ArrayList<>();
        Label uncategorizedLabel = dataAccess.getUncategorizedLabel();
        if (uncategorizedLabel != null) {
            defaultLabels.add(uncategorizedLabel);
        }

        // ----- CATEGORY HANDLING (Updated) -----
        Category category;

        if (categoryInput == null || categoryInput.trim().isEmpty()) {
            // default category if none entered
            category = new Category("Uncategorized");
        } else {
            category = new Category(categoryInput.trim());
        }

        // Create transaction
        Transaction t = new Transaction(
                nextId++,
                amount,
                defaultLabels,
                note,
                date,
                type,
                category
        );

        // Save transaction
        dataAccess.save(t);

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