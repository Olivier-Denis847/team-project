package Olivier;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class OptimizeView extends JPanel{
    private final String viewName = "Optimize";
    private final JPanel timePanel;
    private final JScrollPane labelPane;
    private final JPanel buttonPanel;

    public OptimizeView(String[] labels){
        timePanel = makeTimePanel();
        labelPane = makeLabelPane(labels);
        buttonPanel = makeButtonPanel();
    }

    private JPanel makeTimePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Time (months)"));

        JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 1, 12, 1);
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);

        JLabel sliderValue = new JLabel("Selected: 1 Month", SwingConstants.CENTER);
        slider.addChangeListener(e -> {
            int value = slider.getValue();
            sliderValue.setText("Selected: " + value + (value == 1 ? " Month" : " Months"));
        });

        panel.add(slider, BorderLayout.CENTER);
        panel.add(sliderValue, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel makeCard(String name) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.setMaximumSize(new Dimension(100, 80));
        panel.setPreferredSize(new Dimension(100, 80));

        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] priorities = {"Low", "Medium", "High"};
        JComboBox<String> priorityLabel = new JComboBox<>(priorities);
        priorityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(priorityLabel);

        return panel;
    }

    private JScrollPane makeLabelPane(String[] labels){
         JPanel labelPanel = new JPanel();
         labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));

         for  (String label : labels){
             labelPanel.add(makeCard(label));
         }

         JScrollPane scrollPane = new JScrollPane(labelPanel);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Labels"));
         scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

         return scrollPane;
    }

    private JPanel makeButtonPanel(){
        JPanel panel = new JPanel();
        JButton goButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        panel.add(goButton);
        panel.add(cancelButton);

        return panel;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Optimize View");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String[] labels = {"A", "B", "C", "D", "E", "F", "G", "H"};
        OptimizeView view = new OptimizeView(labels);

        frame.add(view.timePanel, BorderLayout.NORTH);
        frame.add(view.labelPane, BorderLayout.CENTER);
        frame.add(view.buttonPanel, BorderLayout.SOUTH);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
