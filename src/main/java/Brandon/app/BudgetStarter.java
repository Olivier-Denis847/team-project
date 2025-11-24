package Brandon.app;

import Brandon.data.InMemoryBudgetDataAccess;
import Brandon.interfaceAdapter.BudgetController;
import Brandon.interfaceAdapter.BudgetPresenter;
import Brandon.interfaceAdapter.BudgetViewModel;
import Brandon.useCase.BudgetDataAccessInterface;
import Brandon.useCase.BudgetInteractor;
import Brandon.view.CheckBudgetView;
import Brandon.view.MainMenuView;
import Brandon.view.SetBudgetView;
import Brandon.view.YearOverviewView;

import javax.swing.*;
import java.awt.*;

public class BudgetStarter {

    private static final String MAIN_MENU = "MAIN_MENU";
    private static final String ADD_BUDGET = "ADD_BUDGET";
    private static final String CHECK_BUDGET = "CHECK_BUDGET";
    private static final String YEAR_OVERVIEW = "YEAR_OVERVIEW";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // Shared data access
            BudgetDataAccessInterface dataAccess = new InMemoryBudgetDataAccess();

            // Set Budget pipeline
            BudgetViewModel setBudgetViewModel = new BudgetViewModel();
            BudgetPresenter setBudgetPresenter = new BudgetPresenter(setBudgetViewModel);
            BudgetInteractor setBudgetInteractor =
                    new BudgetInteractor(dataAccess, setBudgetPresenter);
            BudgetController setBudgetController =
                    new BudgetController(setBudgetInteractor);

            // Root panel with CardLayout
            CardLayout cardLayout = new CardLayout();
            JPanel root = new JPanel(cardLayout);

            // Main menu view
            MainMenuView mainMenuView = new MainMenuView(
                    () -> cardLayout.show(root, ADD_BUDGET),
                    () -> cardLayout.show(root, CHECK_BUDGET),
                    () -> cardLayout.show(root, YEAR_OVERVIEW)
            );

            // Add SetBudgetView
            SetBudgetView addBudgetView = new SetBudgetView(setBudgetController, setBudgetViewModel,
                    () -> cardLayout.show(root, MAIN_MENU)
            );

            // Add CheckBudgetView - Add Budget button that jumps directly to ADD_BUDGET
            CheckBudgetView checkBudgetView = new CheckBudgetView(dataAccess, () -> cardLayout.show(root, MAIN_MENU),
                    monthKey -> {addBudgetView.setMonthYearFromKey(monthKey); cardLayout.show(root, ADD_BUDGET);}
            );

            YearOverviewView yearOverviewView =
                    new YearOverviewView(dataAccess, () -> cardLayout.show(root, MAIN_MENU));

            // Add YearOverviewView
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
            frame.setMinimumSize(new Dimension(600, 600));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Start on main menu
            cardLayout.show(root, MAIN_MENU);
        });
    }
}