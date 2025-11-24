package Qi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LabelView extends JFrame {

    private JTextField nameField = new JTextField(20);
    private JTextField amountField = new JTextField(20);
    private JTextArea descriptionField = new JTextArea(4, 20);

    // CHANGED: Replaced TextField with ComboBox
    private String[] colorOptions = {
            "Red", "Blue", "Green", "Yellow", "Orange",
            "Pink", "Cyan", "Magenta", "Gray", "Black"
    };
    private JComboBox<String> colorBox = new JComboBox<>(colorOptions);

    // REMOVED: dateField (Transaction already records date)

    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private JButton deleteButton = new JButton("Delete");

    public LabelView(LabelController labelController, Label existingLabel, int userId) {
        setTitle(existingLabel == null ? "Create Label" : "Edit Label");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contentPanel = new JPanel();
        // Reduced row count since Date is removed
        contentPanel.setLayout(new GridLayout(8, 1, 5, 5));

        contentPanel.add(new JLabel("Name:"));
        contentPanel.add(nameField);

        contentPanel.add(new JLabel("Amount:"));
        contentPanel.add(amountField);

        // REMOVED: Date input UI components

        contentPanel.add(new JLabel("Options / Color:"));
        contentPanel.add(colorBox); // Added ComboBox here

        contentPanel.add(new JLabel("Description:"));
        contentPanel.add(descriptionField);

        panel.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // PRE-FILL DATA (If editing)
        if (existingLabel != null) {
            nameField.setText(existingLabel.getLabelName());
            amountField.setText(String.valueOf(existingLabel.getAmount()));

            // REMOVED: Date formatting logic

            colorBox.setSelectedItem(existingLabel.getColor()); // Set selected dropdown item
            descriptionField.setText(existingLabel.getDescription());
        }

        // SAVE ACTION
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                double amount = Double.parseDouble(amountField.getText().trim());
                String color = (String) colorBox.getSelectedItem(); // Get from Dropdown
                String desc = descriptionField.getText().trim();

                String result;

                if (existingLabel == null) {
                    // CREATE NEW
                    // Note: Removed 'date' argument to match LabelController
                    result = labelController.createLabel(name, amount, desc, color, userId);
                } else {
                    // EDIT EXISTING
                    existingLabel.setLabelName(name);
                    existingLabel.setAmount(amount);
                    existingLabel.setColor(color);
                    existingLabel.setDescription(desc);
                    // REMOVED: existingLabel.setLabelDate(date);

                    result = labelController.editLabel(existingLabel);
                }

                JOptionPane.showMessageDialog(this, result);

                // CLOSE WINDOW ON SUCCESS
                if (result.equals("Label created successfully.") || result.equals("Label updated successfully.")) {
                    dispose();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Amount must be a valid number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // DELETE ACTION
        deleteButton.addActionListener(e -> {
            if (existingLabel == null) {
                JOptionPane.showMessageDialog(this, "No label to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this label?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String result = labelController.deleteLabel(existingLabel.getLabelId());
                JOptionPane.showMessageDialog(this, result);
                if (result.equals("Label deleted successfully.")) {
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(e -> dispose());
    }
}