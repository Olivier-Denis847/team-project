package app;

import data_access.ALEDataAccess;
import data_access.LabelDataAccess;
import entity.Label;
import interface_adapter.label.LabelController;
import use_case.LabelUserCase;
import use_case.LabelUserCaseImp;
import view.ExpenseView;

import javax.swing.*;

public class LabelExpenseApp {
    public static void main(String[] args) {
        // Ensure the GUI runs on the Event Dispatch Thread (standard Swing practice)
        SwingUtilities.invokeLater(() -> {

            // 1. Initialize Data Access Objects (The Database layer)
            LabelDataAccess labelDAO = new LabelDataAccess();
            ALEDataAccess aleDAO = new ALEDataAccess();

            // 2. Initialize Use Cases (The Business Logic layer)
            // Inject the DAOs into the Use Case
            LabelUserCase labelUseCase = new LabelUserCaseImp(labelDAO, aleDAO);

            // 3. Initialize Controller (The Interface Adapter layer)
            // Inject the Use Case into the Controller
            LabelController labelController = new LabelController(labelUseCase);

            // --- Optional: Add Dummy Data for Testing ---
            // This ensures you see something in the list when the app starts
            Label dummyLabel1 = new Label(0, "Groceries", "Green", 101, 50.0, "Weekly food");
            labelDAO.createLabel(dummyLabel1);

            Label dummyLabel2 = new Label(0, "Utilities", "Blue", 101, 120.0, "Internet & Hydro");
            labelDAO.createLabel(dummyLabel2);

            Label dummyLabel3 = new Label(0, "Report", "Display Graph", 101, 0.0, "Show analytics");
            labelDAO.createLabel(dummyLabel3);
            // ---------------------------------------------

            // 4. Launch the Main View
            // We use a dummy userId '101' for this demo
            int demoUserId = 101;
            ExpenseView mainView = new ExpenseView(labelController, demoUserId);

            // Set basic frame properties if not fully set in ExpenseView
            // (ExpenseView already does some of this, but this ensures it opens correctly)
            mainView.setTitle("Expense Tracking Demo");
            mainView.setLocationRelativeTo(null); // Center on screen
            mainView.setVisible(true);
        });
    }
}