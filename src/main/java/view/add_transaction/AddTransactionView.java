package view.add_transaction;

import interface_adapter.add_transaction.AddTransactionController;
import interface_adapter.add_transaction.AddTransactionViewModel;

import javax.swing.*;
import java.awt.*;

public class AddTransactionView extends JFrame {
    private AddTransactionController controller;
    private final AddTransactionViewModel viewModel;
    private final JTextArea outputArea = new JTextArea(8, 30);

    public AddTransactionView(AddTransactionViewModel viewModel) {
        this.viewModel = viewModel;

        setTitle("Add Transaction");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JTextField amountField = new JTextField(10);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Income", "Expense"});
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

        viewModel.setOnUpdate(() -> {
            outputArea.append(viewModel.getMessage());
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
