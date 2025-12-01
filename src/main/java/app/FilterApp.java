package app;

import data_access.FinanceDataAccess;
import interface_adapter.filter.FilterTransactionsController;
import interface_adapter.filter.FilterTransactionsPresenter;
import interface_adapter.filter.FilterTransactionsViewModel;
import use_case.add_transaction.TransactionDataAccessInterface;
import use_case.filter_transactions.FilterTransactionsInputBoundary;
import use_case.filter_transactions.FilterTransactionsInteractor;
import use_case.filter_transactions.FilterTransactionsOutputBoundary;
import view.filter_transactions.FilterTransactionsView;

public class FilterApp {
    public static void main(String[] args) {
        //set up data access
        TransactionDataAccessInterface dataAccess = new FinanceDataAccess();

        FilterTransactionsViewModel viewModel = new FilterTransactionsViewModel();
        FilterTransactionsOutputBoundary presenter = new FilterTransactionsPresenter(viewModel);
        FilterTransactionsInputBoundary interactor = new FilterTransactionsInteractor(dataAccess, presenter);
        FilterTransactionsController controller = new FilterTransactionsController(interactor);

        new FilterTransactionsView(controller, viewModel);

    }
}

