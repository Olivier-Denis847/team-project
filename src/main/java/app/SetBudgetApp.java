package app;

import data_access.InMemorySetBudgetDataAccess;
import interface_adapter.budget.SetBudgetController;
import interface_adapter.budget.SetBudgetPresenter;
import interface_adapter.budget.SetBudgetViewModel;
import use_case.budget.SetBudgetDataAccessInterface;
import use_case.budget.SetBudgetInteractor;
import view.budget.CheckBudgetView;
import view.budget.MainMenuView;
import view.budget.SetBudgetView;
import view.budget.YearOverviewView;

import javax.swing.*;
import java.awt.*;

public class SetBudgetApp {

    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String ADD_BUDGET = "ADD_BUDGET";
    private static final String CHECK_BUDGET = "CHECK_BUDGET";
    private static final String YEAR_OVERVIEW = "YEAR_OVERVIEW";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Shared data access
            SetBudgetDataAccessInterface dataAccess = new InMemorySetBudgetDataAccess();

            // Set Budget pipeline
            SetBudgetViewModel setBudgetViewModel = new SetBudgetViewModel();
            SetBudgetPresenter setBudgetPresenter = new SetBudgetPresenter(setBudgetViewModel);
            SetBudgetInteractor setBudgetInteractor =
                    new SetBudgetInteractor(dataAccess, setBudgetPresenter);
            SetBudgetController setBudgetController =
                    new SetBudgetController(setBudgetInteractor);

            // Root panel with CardLayout
            CardLayout cardLayout = new CardLayout();
            JPanel root = new JPanel(cardLayout);

            // Main menu view
            MainMenuView mainMenuView = new MainMenuView(
                    () -> cardLayout.show(root, ADD_BUDGET),
                    () -> cardLayout.show(root, CHECK_BUDGET),
                    () -> cardLayout.show(root, YEAR_OVERVIEW)
            );

            // Add Budget view (SetBudgetView) with back to menu
            SetBudgetView addBudgetView = new SetBudgetView(setBudgetController, setBudgetViewModel,
                    () -> cardLayout.show(root, MAIN_MENU)
            );

            // Check Budget view with:
            // - back to menu
            // - Add Budget button that jumps directly to ADD_BUDGET
            CheckBudgetView checkBudgetView = new CheckBudgetView(dataAccess, () -> cardLayout.show(root, MAIN_MENU),
                    () -> cardLayout.show(root, ADD_BUDGET)
            );

            YearOverviewView yearOverviewView = new YearOverviewView(() -> cardLayout.show(root, MAIN_MENU));

            // Placeholder full-year view
            JPanel fullYearView = new JPanel();
            fullYearView.add(new JLabel("Full Year Budget view coming soon."));

            // Add all views to the root card panel
            root.add(mainMenuView, MAIN_MENU);
            root.add(addBudgetView, ADD_BUDGET);
            root.add(checkBudgetView, CHECK_BUDGET);
            root.add(yearOverviewView, YEAR_OVERVIEW);

            // Frame setup
            JFrame frame = new JFrame("Budget Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(root);
            frame.setMinimumSize(new Dimension(400, 400));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            frame.setResizable(false);


            // Start on main menu
            cardLayout.show(root, MAIN_MENU);
        });
    }
}