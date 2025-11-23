package view;

import data_access.JsonTransactionDataAccessObject;
import interface_adapter.AddTransaction.*;
import use_case.addtransaction.*;


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