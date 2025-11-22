package Qi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ExpenseView extends JFrame {

    private JList<Label> labelList;
    private DefaultListModel<Label> labelListModel;

    private JButton addLabelButton = new JButton("Add Label");
    private JButton editLabelButton = new JButton("Edit Selected");
    private JButton deleteLabelButton = new JButton("Delete Selected");

    public ExpenseView(LabelController labelController, int userId) {

        setTitle("Labels for Expense");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //---------------------- MAIN PANEL ----------------------
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //---------------------- LABEL LIST ----------------------
        labelListModel = new DefaultListModel<>();

        // load labels
        List<Label> labels = labelController.getAllLabels(userId);
        for (Label l : labels) {
            labelListModel.addElement(l);
        }

        labelList = new JList<>(labelListModel);
        labelList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        labelList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Label) {
                    Label lbl = (Label) value;
                    label.setText(lbl.getLabelName());

                    // Try to set color
                    try {
                        Color color;
                        String colorStr = lbl.getColor();

                        // Allow both names ("Red") and hex codes ("#FF0000")
                        if (colorStr.startsWith("#")) {
                            color = Color.decode(colorStr);
                        } else {
                            color = (Color) Color.class.getField(colorStr.toLowerCase()).get(null);
                        }

                        label.setBackground(color);

                        // Change text color for visibility
                        label.setForeground(getContrastColor(color));
                    } catch (Exception e) {
                        // Default color if parsing fails
                        label.setBackground(Color.LIGHT_GRAY);
                        label.setForeground(Color.BLACK);
                    }
                }

                label.setOpaque(true);
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(labelList);
        panel.add(scrollPane, BorderLayout.CENTER);

        //---------------------- BUTTON PANEL ----------------------
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnPanel.add(addLabelButton);
        btnPanel.add(editLabelButton);
        btnPanel.add(deleteLabelButton);

        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);

        //---------------------- ACTIONS ----------------------

        // ADD LABEL
        addLabelButton.addActionListener(e -> {
            LabelView lv = new LabelView(labelController, null, userId);
            lv.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    refreshList(labelController, userId);
                }
            });
            lv.setVisible(true);
        });

        // EDIT LABEL
        editLabelButton.addActionListener(e -> {
            Label selected = labelList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a label to edit.");
                return;
            }

            LabelView lv = new LabelView(labelController, selected, userId);
            lv.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    refreshList(labelController, userId);
                }
            });
            lv.setVisible(true);
        });

        // DELETE LABEL
        deleteLabelButton.addActionListener(e -> {
            Label selected = labelList.getSelectedValue();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a label to delete.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete selected label?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String result = labelController.deleteLabel(selected.getLabelId());
                JOptionPane.showMessageDialog(this, result);
                refreshList(labelController, userId);
            }
        });
    }

    //---------------------- REFRESH LIST ----------------------
    private void refreshList(LabelController labelController, int userId) {
        labelListModel.clear();
        List<Label> labels = labelController.getAllLabels(userId);

        for (Label l : labels) {
            labelListModel.addElement(l);
        }
    }

    private Color getContrastColor(Color color){
        double y = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
        return y >= 128 ? Color.BLACK : Color.WHITE;
    }
    }



