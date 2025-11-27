package view.budget;

import entity.Budget;
import use_case.budget.SetBudgetDataAccessInterface;

import javax.swing.*;
import java.awt.*;

public class CheckBudgetView extends JPanel {

    private final SetBudgetDataAccessInterface dataAccess;
    private final Runnable onBackToMenu;
    private final Runnable onAddBudget;

    // Month & year dropdowns
    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;

    private final JButton checkButton = new JButton("Check Budget");
    private final JButton addBudgetButton = new JButton("Add Budget");

    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel limitLabel = new JLabel("Limit: ");
    private final JLabel spentLabel = new JLabel("Total spent: ");
    private final JLabel remainingLabel = new JLabel("Remaining: ");

    // Split status into two labels so only the value is coloured
    private final JLabel statusTitleLabel = new JLabel("Status: ");
    private final JLabel statusValueLabel = new JLabel("");

    public CheckBudgetView(SetBudgetDataAccessInterface dataAccess,
                           Runnable onBackToMenu,
                           Runnable onAddBudget) {
        this.dataAccess = dataAccess;
        this.onBackToMenu = onBackToMenu;
        this.onAddBudget = onAddBudget;

        // --- month & year models ---
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        monthCombo = new JComboBox<>(months);

        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int y = 2006; y <= 2030; y++) {
            yearModel.addElement(y);
        }
        yearCombo = new JComboBox<>(yearModel);

        setLayout(new BorderLayout());

        // TOP: Back button
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            if (onBackToMenu != null) onBackToMenu.run();
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Month + Year row: [Month][Year]
        JPanel monthPanel = new JPanel();
        monthPanel.add(new JLabel("Month: "));
        monthPanel.add(monthCombo);
        monthPanel.add(new JLabel("Year: "));
        monthPanel.add(yearCombo);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(checkButton);
        buttonPanel.add(addBudgetButton);

        // Message row, centered
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(messageLabel);

        // Info panel with limit / spent / remaining / status
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));
        infoPanel.add(limitLabel);
        infoPanel.add(spentLabel);
        infoPanel.add(remainingLabel);

        // Status row: "Status: " [value]
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(statusTitleLabel);
        statusPanel.add(statusValueLabel);
        infoPanel.add(statusPanel);

        content.add(monthPanel);
        content.add(buttonPanel);
        content.add(messagePanel);
        content.add(Box.createVerticalStrut(10));
        content.add(infoPanel);

        add(content, BorderLayout.CENTER);

        // Button handlers
        checkButton.addActionListener(e -> handleCheckBudget());
        addBudgetButton.addActionListener(e -> {
            if (onAddBudget != null) onAddBudget.run();
        });
    }

    private void handleCheckBudget() {
        // Build month key as "YYYY-MM" from dropdowns
        Integer year = (Integer) yearCombo.getSelectedItem();
        int monthIndex = monthCombo.getSelectedIndex(); // 0–11
        int monthNumber = monthIndex + 1;               // 1–12
        String monthKey = year + "-" + String.format("%02d", monthNumber);

        Budget budget = dataAccess.getBudgetForMonth(monthKey);

        if (budget == null) {
            messageLabel.setText("No budget found for " + monthKey + ".");
            clearBudgetLabels();
        } else {
            messageLabel.setText("Budget found for " + monthKey + ".");
            limitLabel.setText("Limit: " + budget.getLimit());
            spentLabel.setText("Total spent: " + budget.getTotalSpent());
            remainingLabel.setText("Remaining: " + budget.getRemaining());
            statusValueLabel.setText(budget.getStatus());

            if (budget.getStatus().equals("On track")) {
                statusValueLabel.setForeground(new Color(4, 201, 4));
            } else {
                statusValueLabel.setForeground(Color.RED);
            }
        }
    }

    private void clearBudgetLabels() {
        limitLabel.setText("Limit: ");
        spentLabel.setText("Total spent: ");
        remainingLabel.setText("Remaining: ");
        statusValueLabel.setText("");
        statusValueLabel.setForeground(Color.BLACK);
    }
}