package Olivier;

import Olivier.interface_adapter.OptimizeState;
import Olivier.interface_adapter.OptimizeController;
import Olivier.interface_adapter.OptimizeViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class OptimizeView extends JPanel implements PropertyChangeListener{
    private final String viewName = "optimize expenses";

    private final ArrayList<JComboBox<String>> priorityBoxes = new ArrayList<>();
    private final JButton goButton = new JButton("Confirm");
    private final JButton cancelButton = new JButton("Cancel");

    private final OptimizeViewModel viewModel;
    private OptimizeController controller = null;

    public OptimizeView(OptimizeViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.addPropertyChangeListener(this);

        JPanel timePanel = makeTimePanel();
        JScrollPane labelPane = makeLabelPane(viewModel.getState().getLabels());
        JPanel buttonPanel = makeButtonPanel();

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptimizeState s = viewModel.getState();
                controller.execute(
                        s.getTime(),
                        s.getLabels(),
                        s.getPriorities()
                );
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller != null) controller.cancel();
            }
        });

        addPriorityListeners();

        this.setLayout(new BorderLayout());
        this.add(timePanel, BorderLayout.NORTH);
        this.add(labelPane, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addPriorityListeners(){
        for (int i = 0; i < priorityBoxes.size(); i++) {
            priorityBoxes.get(i).addActionListener(e -> {
                ArrayList<String> priorities = new ArrayList<>();
                for (JComboBox<String> box : priorityBoxes) {
                    priorities.add((String) box.getSelectedItem());
                }
                viewModel.updatePriorities(priorities.toArray(new String[0]));
            });
        }
    }

    private JPanel makeTimePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Time (months)"));

        JSlider timeSlider = new JSlider(SwingConstants.HORIZONTAL, 1, 12, 1);
        timeSlider.setMajorTickSpacing(1);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timeSlider.setSnapToTicks(true);

        JLabel sliderValue = new JLabel("Selected: 1 Month", SwingConstants.CENTER);
        timeSlider.addChangeListener(e -> {
            int value = timeSlider.getValue();
            sliderValue.setText("Selected: " + value + (value == 1 ? " Month" : " Months"));
            if (!timeSlider.getValueIsAdjusting()) {
                viewModel.updateTime(value);
            }
        });
        viewModel.updateTime(1);

        panel.add(timeSlider, BorderLayout.CENTER);
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
        this.priorityBoxes.add(priorityLabel);
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

        ArrayList<String> priorities = new ArrayList<>();
        for (JComboBox<String> box : priorityBoxes) {
            priorities.add((String) box.getSelectedItem());
        }
        viewModel.updatePriorities(priorities.toArray(new String[0]));

         JScrollPane scrollPane = new JScrollPane(labelPanel);
         scrollPane.setBorder(BorderFactory.createTitledBorder("Labels"));
         scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
         scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

         return scrollPane;
    }

    private JPanel makeButtonPanel(){
        JPanel panel = new JPanel();
        panel.add(goButton);
        panel.add(cancelButton);

        return panel;
    }

    public void setController(OptimizeController controller) {this.controller = controller;}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        OptimizeState state = (OptimizeState) evt.getNewValue();

        if (state.getError() != null) {
            JOptionPane.showMessageDialog(this, state.getError(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (state.getResult() != null) {
            JOptionPane.showMessageDialog(this, state.getResult(), "Results", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
