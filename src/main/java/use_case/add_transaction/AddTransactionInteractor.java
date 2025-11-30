package use_case.add_transaction;
import entity.Transaction;
import entity.Label;
import java.util.*;

public class AddTransactionInteractor implements AddTransactionInputBoundary {
    private final AddTransactionOutputBoundary presenter;
    private final TransactionDataAccessInterface dataAccess;
    private int nextId = 1;

    public AddTransactionInteractor(AddTransactionOutputBoundary presenter,  TransactionDataAccessInterface dataAccess) {
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

        Transaction t = new Transaction(nextId++, amount, new ArrayList<Label>(), note, date, type);

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
