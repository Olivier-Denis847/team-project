package app;

import use_case.budget.*;
import interface_adapter.budget.*;
import use_case.budget.BudgetInteractor;
import view.budget.*;
import data_access.InMemoryBudgetDataAccess;

import javax.swing.*;
import java.awt.*;

public class BudgetApp {

    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String ADD_BUDGET = "ADD_BUDGET";
    private static final String CHECK_BUDGET = "CHECK_BUDGET";
    private static final String YEAR_OVERVIEW = "YEAR_OVERVIEW";

    public static void start() {
        SwingUtilities.invokeLater(() -> {

            // Shared data access
            BudgetDataAccessInterface dataAccess = new InMemoryBudgetDataAccess();

            // Set Budget pipeline
            BudgetViewModel setBudgetViewModel = new BudgetViewModel();
            BudgetPresenter setBudgetPresenter = new BudgetPresenter(setBudgetViewModel);
            BudgetInteractor setBudgetInteractor = new BudgetInteractor(dataAccess, setBudgetPresenter);
            BudgetController setBudgetController = new BudgetController(setBudgetInteractor);

            // Root panel with CardLayout
            CardLayout cardLayout = new CardLayout();
            JPanel root = new JPanel(cardLayout);

            // Main menu view
            MainMenuView mainMenuView = new MainMenuView(
                    () -> cardLayout.show(root, ADD_BUDGET),
                    () -> cardLayout.show(root, CHECK_BUDGET),
                    () -> cardLayout.show(root, YEAR_OVERVIEW)
            );

            // Add SetBudgetView and destinations
            SetBudgetView addBudgetView = new SetBudgetView(setBudgetController, setBudgetViewModel,
                    () -> cardLayout.show(root, MAIN_MENU)
            );

            // Add CheckBudgetView and destinations
            CheckBudgetView checkBudgetView = new CheckBudgetView(dataAccess, () -> cardLayout.show(root, MAIN_MENU),
                    monthKey -> {addBudgetView.setMonthYearFromKey(monthKey); cardLayout.show(root, ADD_BUDGET);}
            );

            // Add YearOverviewView and destinations
            YearOverviewView yearOverviewView =
                    new YearOverviewView(dataAccess, () -> cardLayout.show(root, MAIN_MENU),
                            monthKey -> {checkBudgetView.setMonthYearFromKey(monthKey);
                                cardLayout.show(root, CHECK_BUDGET);}
                    );

            // Add all views to the root card panel
            root.add(mainMenuView, MAIN_MENU);
            root.add(addBudgetView, ADD_BUDGET);
            root.add(checkBudgetView, CHECK_BUDGET);
            root.add(yearOverviewView, YEAR_OVERVIEW);

            // Frame setup
            JFrame frame = new JFrame("Budget Manager");
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.setContentPane(root);
            frame.setMinimumSize(new Dimension(600, 600));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start on main menu
            cardLayout.show(root, MAIN_MENU);
        });
    }
}