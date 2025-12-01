package view.filter_transactions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import entity.Label;
import entity.Transaction;
import interface_adapter.filter.FilterTransactionsController;
import interface_adapter.filter.FilterTransactionsViewModel;
import use_case.label.LabelDataAccessInterface;
import java.util.List;

public class FilterTransactionsView extends JFrame {

    private FilterTransactionsViewModel viewModel;

    private JComboBox<String> labelComboBox;
    private JButton filterButton;
    private JTextArea resultsArea;

    public FilterTransactionsView(FilterTransactionsController controller,
            FilterTransactionsViewModel viewModel,
            LabelDataAccessInterface dataAccess) {
        this.viewModel = viewModel;

        setTitle("Filter Transactions by Label");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel: label selection + button
        JPanel topPanel = new JPanel();
        labelComboBox = new JComboBox<>();

        // Populate combo box with available labels
        List<Label> availableLabels = dataAccess.getAllLabelsByUser(1); // Default user ID
        for (Label label : availableLabels) {
            labelComboBox.addItem(label.getLabelName());
        }

        filterButton = new JButton("Filter");

        topPanel.add(new JLabel("Label:"));
        topPanel.add(labelComboBox);
        topPanel.add(filterButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel: results
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // Button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedLabel = (String) labelComboBox.getSelectedItem();
                if (selectedLabel != null) {
                    controller.filter(selectedLabel);
                    updateResults();
                }
            }
        });

        setVisible(true);
    }

    // Corrected: iterate over Transaction objects
    public void updateResults() {
        resultsArea.setText(""); // clear previous
        if (viewModel.getFilteredTransactions() == null)
            return;

        for (Transaction t : viewModel.getFilteredTransactions()) {
            // Build label string
            StringBuilder labelStr = new StringBuilder();
            if (t.getLabels() != null && !t.getLabels().isEmpty()) {
                for (int i = 0; i < t.getLabels().size(); i++) {
                    if (i > 0)
                        labelStr.append(", ");
                    labelStr.append(t.getLabels().get(i).getLabelName());
                }
            } else {
                labelStr.append("None");
            }

            String entry = t.getId() + ": " + t.getNote() + " - $" + t.getAmount()
                    + " [" + labelStr.toString() + "]";
            resultsArea.append(entry + "\n");
        }
    }
}
