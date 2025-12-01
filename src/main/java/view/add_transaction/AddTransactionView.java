package view.add_transaction;

import app.MainApp;
import interface_adapter.add_transaction.AddTransactionController;
import interface_adapter.add_transaction.AddTransactionViewModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AddTransactionView extends JFrame {
    private AddTransactionController controller;
    private final JTextArea outputArea = new JTextArea(8, 30);

    public AddTransactionView(AddTransactionViewModel viewModel, MainApp mainApp, String defaultType) {

        setTitle("Add Transaction");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JTextField amountField = new JTextField(10);
        JComboBox<String> typeBox = new JComboBox<>(new String[] { "Income", "Expense" });
        // Set default selection based on the button clicked
        if (defaultType != null && defaultType.equalsIgnoreCase("Expense")) {
            typeBox.setSelectedItem("Expense");
        } else {
            typeBox.setSelectedItem("Income");
        }
        JTextField noteField = new JTextField(10);
        JButton addButton = new JButton("Add");

        inputPanel.add(new JLabel("Amount"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel("Type:"));
        inputPanel.add(typeBox);
        inputPanel.add(new JLabel("Note:"));
        inputPanel.add(noteField);
        inputPanel.add(addButton);

        add(inputPanel, BorderLayout.NORTH);

        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        viewModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("message".equals(evt.getPropertyName())) {
                    String message = (String) evt.getNewValue();
                    outputArea.append(message + "\n");
                    // Refresh MainApp if transaction was added successfully
                    if (message.contains("saved") || message.contains("successfully") || message.contains("Success")) {
                        if (mainApp != null) {
                            SwingUtilities.invokeLater(() -> mainApp.refreshUI());
                        }
                    }
                }
            }
        });

        addButton.addActionListener(e -> {
            try {
                float amount = Float.parseFloat(amountField.getText());
                String type = typeBox.getSelectedItem().toString();
                String note = noteField.getText();

                if (controller == null) {
                    JOptionPane.showMessageDialog(this, "Controller is not set.");
                    return;
                }

                controller.addTransaction(amount, type, note);

            } catch (NumberFormatException ex) {
                outputArea.append("Invalid Input");
            }
        });

        setVisible(true);
    }

    public void setController(AddTransactionController controller) {
        this.controller = controller;
    }
}