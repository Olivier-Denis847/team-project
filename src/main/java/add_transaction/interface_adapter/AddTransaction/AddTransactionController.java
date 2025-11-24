package add_transaction.interface_adapter.AddTransaction;

import add_transaction.use_case.addtransaction.AddTransactionInputBoundary;
import add_transaction.use_case.addtransaction.AddTransactionRequestModel;

import java.util.Date;

public class AddTransactionController {
    private final AddTransactionInputBoundary AddTransactionInteractor;

    public AddTransactionController(AddTransactionInputBoundary AddTransactionInteractor) {
        this.AddTransactionInteractor = AddTransactionInteractor;
    }

    public void addTransaction(float amount, String type, String note) {
        AddTransactionRequestModel requestModel = new AddTransactionRequestModel(amount, note, type, new Date());
        AddTransactionInteractor.execute(requestModel);

    }
}
