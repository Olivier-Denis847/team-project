package view.budget;

import interface_adapter.budget.SetBudgetController;
import interface_adapter.budget.SetBudgetViewModel;

import javax.swing.*;
import java.awt.*;

public class SetBudgetView extends JPanel {

    private final SetBudgetController controller;
    private final SetBudgetViewModel viewModel;

    // Month & year dropdowns
    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;

    private final JTextField limitField = new JTextField(10);
    private final JTextField totalSpentField = new JTextField(10);
    private final JLabel remainingLabel = new JLabel("Remaining: ");
    private final JLabel messageLabel = new JLabel(" ");

    public SetBudgetView(SetBudgetController controller,
                         SetBudgetViewModel viewModel,
                         Runnable onBackToMenu) {

        this.controller = controller;
        this.viewModel = viewModel;

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

        // Root layout: top bar + main content
        setLayout(new BorderLayout());

        // TOP: Back button row
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: main content panel with form
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        int panelWidth = 400;
        int panelHeight = 60;

        // Month + Year row: [Month][Year]
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        monthPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        monthPanel.add(new JLabel("Month: "));
        monthPanel.add(monthCombo);
        monthPanel.add(new JLabel("Year: "));
        monthPanel.add(yearCombo);

        // Limit row
        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        limitPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        limitPanel.add(new JLabel("Budget Limit: "));
        limitPanel.add(limitField);

        // Total spent row
        JPanel totalSpentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        totalSpentPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        totalSpentPanel.add(new JLabel("Total Spent: "));
        totalSpentPanel.add(totalSpentField);

        // Set Budget button
        JButton setBtn = new JButton("Set Budget");
        setBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        setBtn.addActionListener(e -> handleSetBudget());

        // Message + remaining labels
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        remainingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to content in vertical order, centered
        content.add(Box.createVerticalGlue());  // push toward center
        content.add(monthPanel);
        content.add(limitPanel);
        content.add(totalSpentPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(setBtn);
        content.add(Box.createVerticalStrut(10));
        content.add(messageLabel);
        content.add(remainingLabel);
        content.add(Box.createVerticalGlue());  // push toward center

        add(content, BorderLayout.CENTER);
    }

    private void handleSetBudget() {
        // Build month key as "YYYY-MM" from dropdowns
        Integer year = (Integer) yearCombo.getSelectedItem();
        int monthIndex = monthCombo.getSelectedIndex(); // 0–11
        int monthNumber = monthIndex + 1;               // 1–12
        String monthKey = year + "-" + String.format("%02d", monthNumber);

        float limit;
        float totalSpent;

        try {
            limit = Float.parseFloat(limitField.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Limit must be a number.");
            remainingLabel.setText("Remaining: ");
            return;
        }

        try {
            totalSpent = Float.parseFloat(totalSpentField.getText());
        } catch (NumberFormatException e) {
            messageLabel.setText("Total spent must be a number.");
            remainingLabel.setText("Remaining: ");
            return;
        }

        controller.setBudget(monthKey, limit, totalSpent);

        // Presenter has updated the ViewModel at this point
        messageLabel.setText(viewModel.getMessage());
        remainingLabel.setText("Remaining: $" + viewModel.getRemaining());
    }
}