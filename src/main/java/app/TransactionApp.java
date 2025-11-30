package app;

import data_access.FinanceDataAccess;
import interface_adapter.add_transaction.AddTransactionController;
import interface_adapter.add_transaction.AddTransactionPresenter;
import interface_adapter.add_transaction.AddTransactionViewModel;
import use_case.add_transaction.AddTransactionInteractor;
import use_case.add_transaction.TransactionDataAccessInterface;
import view.add_transaction.AddTransactionView;

import javax.swing.*;

class TransactionApp {

        /**
         * Show the add transaction view for adding expenses or income
         * 
         * @param dataAccess      the shared FinanceDataAccess instance
         * @param transactionType "Expense" or "Income"
         * @param mainApp         the MainApp instance to refresh after adding
         *                        transaction
         */
        public static void start(FinanceDataAccess dataAccess, String transactionType, MainApp mainApp) {
                SwingUtilities.invokeLater(() -> {
                        AddTransactionViewModel viewModel = new AddTransactionViewModel();
                        AddTransactionPresenter presenter = new AddTransactionPresenter(viewModel);

                        AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, dataAccess);

                        AddTransactionController controller = new AddTransactionController(interactor);

                        AddTransactionView view = new AddTransactionView(viewModel, mainApp, transactionType);
                        view.setController(controller);
                        view.setTitle("Add " + transactionType);
                        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        view.setLocationRelativeTo(null);
                });
        }

        public static void main(String[] args) {
                AddTransactionViewModel viewModel = new AddTransactionViewModel();
                AddTransactionPresenter presenter = new AddTransactionPresenter(viewModel);

                // Use FinanceDataAccess instead of JsonTransactionDataAccessObject
                TransactionDataAccessInterface dataAccess = new FinanceDataAccess();

                AddTransactionInteractor interactor = new AddTransactionInteractor(presenter, dataAccess);

                AddTransactionController controller = new AddTransactionController(interactor);

                AddTransactionView view = new AddTransactionView(viewModel, null, "Income");
                view.setController(controller);

                controller.addTransaction(100, "Income", "Test1");
                controller.addTransaction(200, "Expense", "Test2");
                controller.addTransaction(-100, "Income", "Test3");

                System.out.println(dataAccess.getTransactions());
        }
}