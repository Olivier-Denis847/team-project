// Main.java
package app;

import view.graph.GraphPanel;
import interface_adapter.graph.*;
import use_case.graph.*;

import javax.swing.*;

import entity.Label;
import entity.Transaction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        /* example usage */
        // Set up test data access
        GraphDataAccessInterface dataAccess = new TestDataAccess();

        // Set up presenter and view model
        GraphViewModel viewModel = new GraphViewModel();
        GraphPresenter presenter = new GraphPresenter(viewModel);

        // Set up interactor
        GraphInteractor interactor = new GraphInteractor(dataAccess, presenter);

        // Set up controller
        GraphController controller = new GraphController(interactor);

        // Set up view
        GraphPanel panel = new GraphPanel(viewModel);
        panel.setGraphController(controller);

        // Add panel as a listener to the view model so it receives updates
        viewModel.addPropertyChangeListener(panel);

        // Initial trigger to populate the graph with test data
        controller.execute("Day", "Expense");

        // Show in JFrame
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("GraphPanel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());
            frame.add(panel, BorderLayout.CENTER);
            frame.setVisible(true);
            frame.setBackground(Color.WHITE);
        });
    }

    // TestDataAccess for testing purposes
    static class TestDataAccess implements GraphDataAccessInterface {
        private final List<Transaction> allEntries;
        private String range = "Day";
        private String type = "Expense";

        public TestDataAccess() {
            // Create test labels for categories
            Label food = new Label(1, "Food", new Date(), "red", 1, 10.0, "Meals");
            Label groceries = new Label(2, "Groceries", new Date(), "orange", 1, 50.0, "Shopping");
            Label transport = new Label(3, "Transport", new Date(), "blue", 1, 5.0, "Travel");
            Label utilities = new Label(4, "Utilities", new Date(), "purple", 1, 100.0, "Bills");
            Label entertainment = new Label(5, "Entertainment", new Date(), "pink", 1, 30.0, "Movies/Games");
            Label salary = new Label(6, "Salary", new Date(), "green", 1, 1000.0, "Monthly income");
            Label bonus = new Label(7, "Bonus", new Date(), "lightgreen", 1, 500.0, "Extra income");
            Label freelance = new Label(8, "Freelance", new Date(), "darkgreen", 1, 200.0, "Side gig");

            allEntries = new ArrayList<>();
            Calendar cal = Calendar.getInstance();

            // Start roughly 4 years ago
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, -4);

            int id = 1;

            // Generate ~220 entries, one every ~7 days over 4 years
            for (int i = 0; i < 220; i++) {
                int pattern = i % 16;
                String tType;
                float amount;
                String note;
                List<Label> labels;

                switch (pattern) {
                    case 0:
                        // Groceries
                        tType = "Expense";
                        amount = 65.00f;
                        note = "Weekly groceries at supermarket";
                        labels = Arrays.asList(groceries);
                        break;
                    case 1:
                        // Food / eating out
                        tType = "Expense";
                        amount = 18.50f;
                        note = "Lunch with friends";
                        labels = Arrays.asList(food);
                        break;
                    case 2:
                        // Transport small expense
                        tType = "Expense";
                        amount = 3.25f;
                        note = "Bus/metro fare";
                        labels = Arrays.asList(transport);
                        break;
                    case 3:
                        // Utilities
                        tType = "Expense";
                        amount = 85.00f;
                        note = "Electricity bill";
                        labels = Arrays.asList(utilities);
                        break;
                    case 4:
                        // Entertainment small
                        tType = "Expense";
                        amount = 14.99f;
                        note = "Streaming subscription";
                        labels = Arrays.asList(entertainment);
                        break;
                    case 5:
                        // Groceries larger
                        tType = "Expense";
                        amount = 95.75f;
                        note = "Big grocery trip (stock-up)";
                        labels = Arrays.asList(groceries);
                        break;
                    case 6:
                        // Salary income
                        tType = "Income";
                        amount = 2500.00f;
                        note = "Monthly salary";
                        labels = Arrays.asList(salary);
                        break;
                    case 7:
                        // Freelance income
                        tType = "Income";
                        amount = 220.00f;
                        note = "Freelance project payment";
                        labels = Arrays.asList(freelance);
                        break;
                    case 8:
                        // Bonus income
                        tType = "Income";
                        amount = 500.00f;
                        note = "Performance bonus";
                        labels = Arrays.asList(bonus);
                        break;
                    case 9:
                        // Dinner + movie (multi-label)
                        tType = "Expense";
                        amount = 42.00f;
                        note = "Dinner out and movie";
                        labels = Arrays.asList(food, entertainment);
                        break;
                    case 10:
                        // Groceries + utilities (multi-label)
                        tType = "Expense";
                        amount = 135.00f;
                        note = "Groceries and household supplies";
                        labels = Arrays.asList(groceries, utilities);
                        break;
                    case 11:
                        // Transport higher
                        tType = "Expense";
                        amount = 45.00f;
                        note = "Gas for car";
                        labels = Arrays.asList(transport);
                        break;
                    case 12:
                        // Utility internet bill
                        tType = "Expense";
                        amount = 70.00f;
                        note = "Internet bill";
                        labels = Arrays.asList(utilities);
                        break;
                    case 13:
                        // Entertainment bigger
                        tType = "Expense";
                        amount = 60.00f;
                        note = "Concert tickets";
                        labels = Arrays.asList(entertainment);
                        break;
                    case 14:
                        // Food coffee/breakfast
                        tType = "Expense";
                        amount = 9.75f;
                        note = "Coffee and breakfast";
                        labels = Arrays.asList(food);
                        break;
                    case 15:
                    default:
                        // Mixed small day (food + transport)
                        tType = "Expense";
                        amount = 22.25f;
                        note = "Lunch and transport";
                        labels = Arrays.asList(food, transport);
                        break;
                }

                allEntries.add(
                        new Transaction(id++, amount, labels, note, cal.getTime(), tType));

                // Move roughly one week forward
                cal.add(Calendar.DAY_OF_MONTH, 7);
            }

            // Add a few denser recent days (last month) to make short-range graphs
            // interesting
            cal.setTime(new Date());

            // Today - multiple entries
            Date today = cal.getTime();
            allEntries.add(new Transaction(id++, 16.50f, Arrays.asList(food),
                    "Coffee and breakfast (today)", today, "Expense"));
            allEntries.add(new Transaction(id++, 52.30f, Arrays.asList(groceries),
                    "Weekly groceries (today)", today, "Expense"));
            allEntries.add(new Transaction(id++, 7.10f, Arrays.asList(transport),
                    "Uber ride (today)", today, "Expense"));

            // Yesterday
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = cal.getTime();
            allEntries.add(new Transaction(id++, 24.00f, Arrays.asList(food, entertainment),
                    "Dinner and movie (yesterday)", yesterday, "Expense"));
            allEntries.add(new Transaction(id++, 11.50f, Arrays.asList(transport),
                    "Taxi ride (yesterday)", yesterday, "Expense"));

            // 3 days ago
            cal.add(Calendar.DAY_OF_MONTH, -2);
            Date threeDaysAgo = cal.getTime();
            allEntries.add(new Transaction(id++, 120.00f, Arrays.asList(utilities),
                    "Electric bill (this month)", threeDaysAgo, "Expense"));
            allEntries.add(new Transaction(id++, 32.00f, Arrays.asList(entertainment),
                    "Game purchase", threeDaysAgo, "Expense"));

            // 1 week ago - salary
            cal.add(Calendar.DAY_OF_MONTH, -4);
            Date oneWeekAgo = cal.getTime();
            allEntries.add(new Transaction(id++, 2600.00f, Arrays.asList(salary),
                    "Monthly salary (recent)", oneWeekAgo, "Income"));
            allEntries.add(new Transaction(id++, 180.00f, Arrays.asList(freelance),
                    "Freelance design work", oneWeekAgo, "Income"));
        }

        @Override
        public List<Transaction> getAllEntries() {
            return this.allEntries;
        }

        @Override
        public void saveGraphRange(String lineGraphRange) {
            this.range = lineGraphRange;
        }

        @Override
        public void saveGraphType(String type) {
            this.type = type;
        }

        @Override
        public String getRange() {
            return this.range;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public List<Transaction> getExpenses() {
            List<Transaction> expenses = new ArrayList<>();
            for (Transaction t : this.allEntries) {
                if ("Expense".equalsIgnoreCase(t.getType())) {
                    expenses.add(t);
                }
            }
            return expenses;
        }

        @Override
        public List<Transaction> getIncomes() {
            List<Transaction> incomes = new ArrayList<>();
            for (Transaction t : this.allEntries) {
                if ("Income".equalsIgnoreCase(t.getType())) {
                    incomes.add(t);
                }
            }
            return incomes;
        }
    }
}
