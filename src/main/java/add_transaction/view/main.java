package add_transaction.view;

import add_transaction.data_access.JsonTransactionDataAccessObject;
import add_transaction.interface_adapter.AddTransaction.AddTransactionController;
import add_transaction.interface_adapter.AddTransaction.AddTransactionPresenter;
import add_transaction.interface_adapter.AddTransaction.AddTransactionViewModel;
import add_transaction.use_case.addtransaction.AddTransactionInteractor;
import add_transaction.use_case.addtransaction.TransactionDataAccessInterface;


public class main {
    public static void main(String[] args) {
        AddTransactionViewModel viewModel = new AddTransactionViewModel();
        AddTransactionPresenter presenter = new AddTransactionPresenter(viewModel);

        TransactionDataAccessInterface dataAccess =
                new JsonTransactionDataAccessObject("transactions.json");

        AddTransactionInteractor interactor =
                new AddTransactionInteractor(presenter, dataAccess);

        AddTransactionController controller =
                new AddTransactionController(interactor);

        AddTransactionView view = new AddTransactionView(viewModel);
        view.setController(controller);


        controller.addTransaction(100, "Income", "Test1");
        controller.addTransaction(200, "Expense", "Test2");
        controller.addTransaction(-100, "Income", "Test3");

        System.out.println(dataAccess.getTransactions());
    }
}