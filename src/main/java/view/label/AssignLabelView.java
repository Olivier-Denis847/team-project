package view.label;

import data_access.FinanceDataAccess;
import entity.Label;
import entity.Transaction;
import interface_adapter.label.LabelController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View for assigning labels to a specific transaction
 */
public class AssignLabelView extends JFrame {

    private JList<Label> availableLabelsList;
    private DefaultListModel<Label> availableLabelsModel;
    private JList<Label> assignedLabelsList;
    private DefaultListModel<Label> assignedLabelsModel;

    private Transaction transaction;
    private LabelController labelController;
    private FinanceDataAccess dataAccess;
    private int userId;
    private Runnable onUpdateCallback;

    public AssignLabelView(LabelController labelController, Transaction transaction, int userId,
            Runnable onUpdateCallback, FinanceDataAccess dataAccess) {
        this.labelController = labelController;
        this.transaction = transaction;
        this.userId = userId;
        this.onUpdateCallback = onUpdateCallback;
        this.dataAccess = dataAccess;

        setTitle("Assign Labels - " + transaction.getNote());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Two-column layout
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // Available Labels Panel
        JPanel availablePanel = createLabelListPanel("Available Labels", true);
        listsPanel.add(availablePanel);

        // Assigned Labels Panel
        JPanel assignedPanel = createLabelListPanel("Assigned Labels", false);
        listsPanel.add(assignedPanel);

        mainPanel.add(listsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addNewLabelButton = new JButton("Add");
        JButton editLabelButton = new JButton("Edit");
        JButton deleteLabelButton = new JButton("Delete");
        JButton addButton = new JButton("Assign →");
        JButton removeButton = new JButton("← Remove");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(addNewLabelButton);
        buttonPanel.add(editLabelButton);
        buttonPanel.add(deleteLabelButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load initial data
        refreshLists();

        // Button actions
        addNewLabelButton.addActionListener(e -> {
            LabelView labelView = new LabelView(labelController, null, userId);
            labelView.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    refreshLists(); // Refresh the available labels list after creating new label
                }
            });
            labelView.setVisible(true);
        });
        addButton.addActionListener(e -> assignSelectedLabel());
        removeButton.addActionListener(e -> removeSelectedLabel());
        closeButton.addActionListener(e -> dispose());

        // Edit selected available label
        editLabelButton.addActionListener(e -> {
            Label selected = availableLabelsList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a label to edit.");
                return;
            }
            LabelView lv = new LabelView(labelController, selected, userId);
            lv.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    refreshLists();
                }
            });
            lv.setVisible(true);
        });

        // Delete selected available label
        deleteLabelButton.addActionListener(e -> {
            Label selected = availableLabelsList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Select a label to delete.");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Delete selected label?", "Confirm",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = labelController.deleteLabel(selected.getLabelId());
                JOptionPane.showMessageDialog(this, res);
                refreshLists();
            }
        });
    }

    private JPanel createLabelListPanel(String title, boolean isAvailable) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(title), BorderLayout.NORTH);

        if (isAvailable) {
            availableLabelsModel = new DefaultListModel<>();
            availableLabelsList = new JList<>(availableLabelsModel);
            availableLabelsList.setCellRenderer(createLabelRenderer());
            panel.add(new JScrollPane(availableLabelsList), BorderLayout.CENTER);
        } else {
            assignedLabelsModel = new DefaultListModel<>();
            assignedLabelsList = new JList<>(assignedLabelsModel);
            assignedLabelsList.setCellRenderer(createLabelRenderer());
            panel.add(new JScrollPane(assignedLabelsList), BorderLayout.CENTER);
        }

        return panel;
    }

    private ListCellRenderer<? super Label> createLabelRenderer() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);

                if (value instanceof Label) {
                    Label lbl = (Label) value;
                    label.setText(lbl.getLabelName());

                    // Set background to label's color
                    try {
                        if (lbl.getColor() != null && lbl.getColor().startsWith("#")) {
                            Color color = Color.decode(lbl.getColor());
                            label.setBackground(color);
                            label.setForeground(Color.BLACK);
                        }
                    } catch (Exception e) {
                        label.setBackground(Color.LIGHT_GRAY);
                        label.setForeground(Color.BLACK);
                    }
                }

                label.setOpaque(true);
                return label;
            }
        };
    }

    private void refreshLists() {
        availableLabelsModel.clear();
        assignedLabelsModel.clear();

        // Get all labels
        List<Label> allLabels = labelController.getAllLabels(userId);
        List<Label> assignedLabels = transaction.getLabels();

        // Filter out Uncategorized
        List<Label> assigned = new ArrayList<>();
        if (assignedLabels != null) {
            for (Label l : assignedLabels) {
                if (!"Uncategorized".equalsIgnoreCase(l.getLabelName())) {
                    assigned.add(l);
                }
            }
        }

        // Populate assigned labels
        for (Label l : assigned) {
            assignedLabelsModel.addElement(l);
        }

        // Populate available labels (excluding already assigned and Uncategorized)
        for (Label l : allLabels) {
            if (!"Uncategorized".equalsIgnoreCase(l.getLabelName())) {
                boolean isAssigned = false;
                for (Label assignedLabel : assigned) {
                    if (assignedLabel.getLabelId() == l.getLabelId()) {
                        isAssigned = true;
                        break;
                    }
                }
                if (!isAssigned) {
                    availableLabelsModel.addElement(l);
                }
            }
        }
    }

    private void assignSelectedLabel() {
        Label selectedLabel = availableLabelsList.getSelectedValue();
        if (selectedLabel == null) {
            JOptionPane.showMessageDialog(this, "Please select a label to assign.");
            return;
        }

        try {
            labelController.assignLabelToExpense((int) transaction.getId(), selectedLabel);

            // Reload transaction from storage to get fresh state
            reloadTransaction();
            refreshLists();

            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error assigning label: " + e.getMessage());
        }
    }

    private void removeSelectedLabel() {
        Label selectedLabel = assignedLabelsList.getSelectedValue();
        if (selectedLabel == null) {
            JOptionPane.showMessageDialog(this, "Please select a label to remove.");
            return;
        }

        try {
            // Remove via controller so persistence updates correctly
            labelController.removeLabelFromExpense((int) transaction.getId(), selectedLabel.getLabelId());

            // Reload transaction from storage to get fresh state
            reloadTransaction();
            refreshLists();

            if (onUpdateCallback != null) {
                onUpdateCallback.run();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error removing label: " + e.getMessage());
        }
    }

    private void reloadTransaction() {
        // Reload the transaction from storage to get the updated label list
        List<Transaction> allTransactions = dataAccess.getAll();
        for (Transaction t : allTransactions) {
            if (t.getId() == transaction.getId()) {
                transaction.setLabels(new ArrayList<>(t.getLabels()));
                break;
            }
        }
    }
}
