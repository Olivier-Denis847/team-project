package Qi;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LabelView extends JFrame {

    private JTextField nameField =  new JTextField(20);
    private JTextField amountField = new JTextField(20);
    private JTextArea descriptionField = new JTextArea(4, 20);
    private JTextField dateField = new JTextField(20);
    private JTextField colorField = new JTextField(20);

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
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(10, 1 , 5, 5));

        contentPanel.add(new JLabel("Name:"));
        contentPanel.add(nameField);

        contentPanel.add(new JLabel("Amount:"));
        contentPanel.add(amountField);

        contentPanel.add(new JLabel("Date (yyyy-MM-dd):)"));
        contentPanel.add(dateField);

        contentPanel.add(new JLabel("Color:"));
        contentPanel.add(colorField);

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

        if (existingLabel != null) {
            nameField.setText(existingLabel.getLabelName());
            amountField.setText(String.valueOf(existingLabel.getAmount()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateField.setText(sdf.format(existingLabel.getLabelDate()));

            colorField.setText(existingLabel.getColor());
            descriptionField.setText(existingLabel.getDescription());
        }

        saveButton.addActionListener(e -> {
            if (existingLabel == null) {
                try {
                    String name = nameField.getText().trim();
                    double amount = Double.parseDouble(amountField.getText().trim());
                    String color = colorField.getText().trim();
                    String desc = descriptionField.getText().trim();

                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText().trim());

                    String result = labelController.createLabel(name, amount, date, desc, color, userId);
                    JOptionPane.showMessageDialog(this, result);

                    if (result.equals("Label created successfully.")) {
                        dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                }
            }else{
                try {
                existingLabel.setLabelName(nameField.getText().trim());
                existingLabel.setAmount(Double.parseDouble(amountField.getText().trim()));
                existingLabel.setColor(colorField.getText().trim());
                existingLabel.setDescription(descriptionField.getText().trim());

                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText().trim());
                existingLabel.setLabelDate(date);

                String result = labelController.editLabel(existingLabel);
                JOptionPane.showMessageDialog(this, result);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                }
            }
        });

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


