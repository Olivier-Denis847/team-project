package view.label;

import entity.Label;
import interface_adapter.label.LabelController;

import javax.swing.*;
import java.awt.*;

public class LabelView extends JFrame {

    private final JTextField nameField = new JTextField(20);
    private final JTextArea descriptionField = new JTextArea(4, 20);

    // Color options with hex values (light/pastel colors for readability)
    private final String[] colorNames = { "Red", "Blue", "Green", "Orange", "Purple", "Pink", "Cyan", "Yellow",
            "Teal", "Mint", "Peach", "Lavender" };
    private final String[] colorHexValues = { "#FFB3BA", "#BAE1FF", "#BAFFC9", "#FFD9B3", "#E0BBE4", "#FFC0CB",
            "#B4F8F8", "#FFFFBA", "#B3E5D9", "#C7F9CC", "#FFCBA4", "#E6D5F5" };
    private final JComboBox<String> colorPicker = new JComboBox<>(colorNames);

    public LabelView(LabelController labelController, Label existingLabel, int userId) {
        setTitle(existingLabel == null ? "Create Label" : "Edit Label");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(8, 1, 5, 5));

        contentPanel.add(new JLabel("Name:"));
        contentPanel.add(nameField);

        contentPanel.add(new JLabel("Color:"));
        contentPanel.add(colorPicker);

        contentPanel.add(new JLabel("Description:"));
        contentPanel.add(descriptionField);

        panel.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton saveButton = new JButton("Save");
        buttonPanel.add(saveButton);
        JButton deleteButton = new JButton("Delete");
        buttonPanel.add(deleteButton);
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(cancelButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        if (existingLabel != null) {
            nameField.setText(existingLabel.getLabelName());

            // Find matching color hex and select it
            String existingColor = existingLabel.getColor();
            for (int i = 0; i < colorHexValues.length; i++) {
                if (colorHexValues[i].equalsIgnoreCase(existingColor)) {
                    colorPicker.setSelectedIndex(i);
                    break;
                }
            }

            descriptionField.setText(existingLabel.getDescription());
        }

        saveButton.addActionListener(e -> {
            if (existingLabel == null) {
                try {
                    String name = nameField.getText().trim();
                    int selectedColorIndex = colorPicker.getSelectedIndex();
                    String colorHex = colorHexValues[selectedColorIndex];
                    String desc = descriptionField.getText().trim();

                    String result = labelController.createLabel(name, 0, desc, colorHex, userId);
                    JOptionPane.showMessageDialog(this, result);

                    if (result.equals("Label created successfully.")) {
                        dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
                }
            } else {
                try {
                    existingLabel.setLabelName(nameField.getText().trim());
                    int selectedColorIndex = colorPicker.getSelectedIndex();
                    String colorHex = colorHexValues[selectedColorIndex];
                    existingLabel.setColor(colorHex);
                    existingLabel.setDescription(descriptionField.getText().trim());

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
                    JOptionPane.YES_NO_OPTION);

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
