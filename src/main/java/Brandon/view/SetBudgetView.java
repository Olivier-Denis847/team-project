package Brandon.view;

import Brandon.interfaceAdapter.BudgetController;
import Brandon.interfaceAdapter.BudgetViewModel;

import javax.swing.*;
import java.awt.*;

public class SetBudgetView extends JPanel {

    private final BudgetController controller;
    private final BudgetViewModel viewModel;

    private static final int MIN_YEAR = 2006;
    private static final int MAX_YEAR = 2030;

    // Month & year dropdowns
    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;

    // NEW: Arrows for month switching
    private final JButton prevButton = new JButton("←");
    private final JButton nextButton = new JButton("→");

    private final JTextField limitField = new JTextField(10);
    private final JTextField totalSpentField = new JTextField(10);
    private final JLabel messageLabel = new JLabel(" ");

    private final JButton saveBtn = new JButton("Save");
    private final JButton resetBtn = new JButton("Reset");

    public SetBudgetView(BudgetController controller,
                         BudgetViewModel viewModel,
                         Runnable onBackToMenu) {

        this.controller = controller;
        this.viewModel = viewModel;

        // Month models
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        monthCombo = new JComboBox<>(months);

        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int y = MIN_YEAR; y <= MAX_YEAR; y++) yearModel.addElement(y);
        yearCombo = new JComboBox<>(yearModel);

        // Default year = current system year
        yearCombo.setSelectedItem(java.time.Year.now().getValue());
        monthCombo.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);

        setLayout(new BorderLayout());

        // Top Back button
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            limitField.setText("");
            totalSpentField.setText("");
            messageLabel.setText(" ");
            resetBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            if (onBackToMenu != null) onBackToMenu.run();
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Center content panel
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        int panelWidth = 400, panelHeight = 60;

        // Month + Year + arrows
        JPanel monthPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        monthPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));

        monthPanel.add(prevButton);
        monthPanel.add(new JLabel("Month: "));
        monthPanel.add(monthCombo);
        monthPanel.add(new JLabel("Year: "));
        monthPanel.add(yearCombo);
        monthPanel.add(nextButton);

        // Limit
        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        limitPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        limitPanel.add(new JLabel("Budget Limit: "));
        limitPanel.add(limitField);

        // Total spent
        JPanel spentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        spentPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        spentPanel.add(new JLabel("Total Spent: "));
        spentPanel.add(totalSpentField);

        saveBtn.setEnabled(false);
        resetBtn.setEnabled(false);

        JPanel buttonRow = new JPanel();
        buttonRow.add(resetBtn);
        buttonRow.add(saveBtn);

        // Center message label
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add everything
        content.add(Box.createVerticalGlue());
        content.add(monthPanel);
        content.add(limitPanel);
        content.add(spentPanel);
        content.add(buttonRow);
        content.add(Box.createVerticalStrut(10));
        content.add(messageLabel);
        content.add(Box.createVerticalGlue());
        add(content, BorderLayout.CENTER);

        // Input listeners to enable Save/Reset
        Runnable updateButtons = () -> {
            boolean anyInput = !limitField.getText().isEmpty()
                    || !totalSpentField.getText().isEmpty();
            saveBtn.setEnabled(anyInput);
            resetBtn.setEnabled(anyInput);
        };

        limitField.getDocument().addDocumentListener(new SimpleListener(updateButtons));
        totalSpentField.getDocument().addDocumentListener(new SimpleListener(updateButtons));

        // Button actions
        saveBtn.addActionListener(e -> handleSave());
        resetBtn.addActionListener(e -> {
            limitField.setText("");
            totalSpentField.setText("");
            messageLabel.setText(" ");
            resetBtn.setEnabled(false);
            saveBtn.setEnabled(false);
        });

        prevButton.addActionListener(e -> {
            limitField.setText("");
            totalSpentField.setText("");
            messageLabel.setText(" ");
            resetBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            changeMonth(-1);
        });
        nextButton.addActionListener(e -> {
            limitField.setText("");
            totalSpentField.setText("");
            messageLabel.setText(" ");
            resetBtn.setEnabled(false);
            saveBtn.setEnabled(false);
            changeMonth(1);
        });
    }

    // Save Budget Handler
    private void handleSave() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        int monthIndex = monthCombo.getSelectedIndex();
        int monthNum = monthIndex + 1;

        String monthKey = String.format("%02d", monthNum) + "-" + year;

        // Validation
        if (!limitField.getText().matches("^\\d*\\.?\\d+$")) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Limit must be a non-negative value.");
            viewModel.setSuccess(false);
            return;
        }
        float limit = Float.parseFloat(limitField.getText());

        if (!totalSpentField.getText().matches("^\\d*\\.?\\d+$")) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("Total spent must be non-negative.");
            viewModel.setSuccess(false);
            return;
        }
        float spent = Float.parseFloat(totalSpentField.getText());

        controller.setBudget(monthKey, limit, spent);

        if (viewModel.isSuccess()) {
            messageLabel.setForeground(new Color(4, 201, 4));
        } else {
            messageLabel.setForeground(Color.RED);
        }

        messageLabel.setText(viewModel.getMessage());
    }

    // Month switching (same as CheckBudget)
    private void changeMonth(int change) {
        int m = monthCombo.getSelectedIndex();
        int y = (Integer) yearCombo.getSelectedItem();

        // Prevent out-of-range moves
        if (change == -1 && m == 0 && y == MIN_YEAR) {
            prevButton.setEnabled(false);
            return;
        }
        if (change == 1 && m == 11 && y == MAX_YEAR) {
            nextButton.setEnabled(false);
            return;
        }

        int newMonth = m + change;
        int newYear = y;

        if (newMonth < 0) {
            newMonth = 11;
            newYear--;
        } else if (newMonth > 11) {
            newMonth = 0;
            newYear++;
        }

        monthCombo.setSelectedIndex(newMonth);
        yearCombo.setSelectedItem(newYear);

        // Re-enable arrows when within valid ranges
        prevButton.setEnabled(!(newYear == MIN_YEAR && newMonth == 0));
        nextButton.setEnabled(!(newYear == MAX_YEAR && newMonth == 11));
    }

    // Allow other views (like CheckBudgetView) to set the month/year
    public void setMonthYearFromKey(String monthKey) {
        // monthKey format is "MM-YYYY"
        if (monthKey == null || !monthKey.contains("-")) {
            return;
        }

        String[] parts = monthKey.split("-");
        if (parts.length != 2) {
            return;
        }

        try {
            int monthNumber = Integer.parseInt(parts[0]); // 1–12
            int year = Integer.parseInt(parts[1]);

            // Update combos if values are valid
            if (monthNumber >= 1 && monthNumber <= 12) {
                monthCombo.setSelectedIndex(monthNumber - 1);
            }
            yearCombo.setSelectedItem(year);
        } catch (NumberFormatException e) {
            // ignore bad format
        }
    }
}

// Helper listener
class SimpleListener implements javax.swing.event.DocumentListener {
    private final Runnable callback;
    public SimpleListener(Runnable r) { this.callback = r; }

    public void insertUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
    public void removeUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
    public void changedUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
}
