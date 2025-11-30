package app;

import data_access.FinanceDataAccess;
import interface_adapter.label.LabelController;
import use_case.label.LabelUserCase;
import use_case.label.LabelUserCaseImp;
import data_access.ALEDataAccess;
import view.label.ExpenseView;

import javax.swing.*;

public class LabelApp {

    /**
     * Start the label management view
     * 
     * @param dataAccess the shared FinanceDataAccess instance
     * @param userId     the user ID (default 1 for now)
     */
    public static void start(FinanceDataAccess dataAccess, int userId) {
        SwingUtilities.invokeLater(() -> {
            // Set up use case with FinanceDataAccess (implements both interfaces)
            ALEDataAccess aleDA = new ALEDataAccess();

            LabelUserCase labelUserCase = new LabelUserCaseImp(dataAccess, aleDA);

            // Set up controller
            LabelController controller = new LabelController(labelUserCase);

            // Set up view
            ExpenseView view = new ExpenseView(controller, userId);
            view.setVisible(true);
        });
    }
}
