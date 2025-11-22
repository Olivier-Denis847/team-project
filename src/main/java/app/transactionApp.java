package app;

import interface_adapter.add_transaction.*;
import use_case.add_transaction.*;
import data_access.InMemoryTransactionDataAccessObject;
import view.add_transaction.AddTransactionView;


public class transactionApp {
    public static void main(String[] args) {
        AddTransactionViewModel viewModel = new AddTransactionViewModel();
        AddTransactionPresenter presenter = new AddTransactionPresenter(viewModel);

        InMemoryTransactionDataAccessObject dataAccess =
                new InMemoryTransactionDataAccessObject();

        AddTransactionInteractor interactor =
                new AddTransactionInteractor(presenter, dataAccess);

        AddTransactionController controller =
                new AddTransactionController(interactor);

        AddTransactionView view = new AddTransactionView(viewModel);
        view.setController(controller);


        controller.addTransaction(100, "Income", "Test1");
        controller.addTransaction(200, "Expense", "Test2");

        System.out.println(dataAccess.getTransactions());
    }
}