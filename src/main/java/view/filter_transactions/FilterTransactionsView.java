package view.filter_transactions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import entity.Transaction;
import interface_adapter.filter.FilterTransactionsController;
import interface_adapter.filter.FilterTransactionsViewModel;

public class FilterTransactionsView extends JFrame {

    private FilterTransactionsController controller;
    private FilterTransactionsViewModel viewModel;

    private JComboBox<String> categoryComboBox;
    private JButton filterButton;
    private JTextArea resultsArea;

    public FilterTransactionsView(FilterTransactionsController controller,
                                  FilterTransactionsViewModel viewModel) {
        this.controller = controller;
        this.viewModel = viewModel;

        setTitle("Filter Transactions");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel: category selection + button
        JPanel topPanel = new JPanel();
        categoryComboBox = new JComboBox<>(new String[]{"FOOD", "SHOPPING", "UTILITIES"});
        filterButton = new JButton("Filter");

        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryComboBox);
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
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                controller.filter(selectedCategory);
                updateResults();
            }
        });

        setVisible(true);
    }

    // Corrected: iterate over Transaction objects
    public void updateResults() {
        resultsArea.setText(""); // clear previous
        if (viewModel.getFilteredTransactions() == null) return;

        for (Transaction t : viewModel.getFilteredTransactions()) {
            String entry = t.getId() + ": " + t.getNote() + " - $" + t.getAmount()
                    + " [" + (t.getCategory() != null ? t.getCategory().getCategoryName() : "None") + "]";
            resultsArea.append(entry + "\n");
        }
    }
}

