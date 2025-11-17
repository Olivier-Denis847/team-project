package use_case.AddTransaction;
import Entity.Transaction;
import Entity.Label;
import java.util.*;

public class AddTransactionInteractor implements AddTransactionInputBoundary {
    private final AddTransactionOutputBoundary presenter;
    private final List<Transaction> transactions =  new ArrayList<>();
    private Transaction transaction;
    private final TransactionDataAccessInterface dataAccess;
    private int nextId = 1;

    public AddTransactionInteractor(AddTransactionOutputBoundary presenter,  TransactionDataAccessInterface dataAccess) {
        this.presenter = presenter;
        this.dataAccess = dataAccess;
    }

    @Override
    public void execute(AddTransactionRequestModel requestModel) {
        System.out.println("Interactor reached");
        float amount = requestModel.get_amount();
        String type = requestModel.get_type();
        String note = requestModel.get_note();
        Date date = requestModel.get_date();

        if (amount < 0) {
            presenter.prepareFailureView("Amount must be greater than zero\n");
            return;
        }
        //delete this if branch later
        if (!type.equalsIgnoreCase("income") && !type.equalsIgnoreCase("expense")) {
            presenter.prepareFailureView("Income or Expense is not supported");
        }

        Transaction t = new Transaction(nextId++, amount, new ArrayList<Label>(), note, date, type);
        transactions.add(t);

        dataAccess.save(t);
        AddTransactionResponseModel responseModel = new AddTransactionResponseModel(
                nextId,
                amount,
                type,
                note,
                t.getDate().toString());


        presenter.prepareSuccessView(responseModel);
    }
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
