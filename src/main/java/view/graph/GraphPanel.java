package view.graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import interface_adapter.graph.GraphController;
import interface_adapter.graph.GraphState;
import interface_adapter.graph.GraphViewModel;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

public class GraphPanel extends JPanel implements ActionListener, PropertyChangeListener {
    private JPanel leftGraphContainer;
    private JPanel rightGraphContainer;

    private GraphViewModel gvm;
    private GraphController gc = null;

    private DefaultPieDataset<String> pieDataset;
    private DefaultCategoryDataset barDataset;

    public GraphPanel(GraphViewModel gvm) {
        // graph panel settings
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        // inner container settings
        GridBagConstraints c = new GridBagConstraints();
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new GridLayout(1, 2));
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new GridLayout(1, 2));

        c.fill = GridBagConstraints.BOTH;
        // --- Top row: weight 1 ---
        c.weightx = 1;
        c.weighty = 0; // proportion of height
        c.gridx = 0;
        c.gridy = 0;
        add(topContainer, c);

        // --- Bottom row: weight 2 ---
        c.weighty = 1; // proportion of height
        c.gridy = 1;
        add(bottomContainer, c);

        this.gvm = gvm;

        // range selector buttons
        JPanel rangeSelectorContainer = new JPanel();
        rangeSelectorContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        rangeSelectorContainer.setBackground(Color.WHITE);
        JButton dayButton = new JButton("Day");
        JButton monthButton = new JButton("Month");
        JButton yearButton = new JButton("Year");
        rangeSelectorContainer.add(dayButton);
        rangeSelectorContainer.add(monthButton);
        rangeSelectorContainer.add(yearButton);
        topContainer.add(rangeSelectorContainer);

        /* add listeners for range buttons */
        JButton[] rangeButtons = { dayButton, monthButton, yearButton };

        for (JButton button : rangeButtons) {
            final JButton b = button;
            button.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getSource().equals(b)) {
                                final GraphState currentState = gvm.getState();
                                gc.execute(
                                        b.getText(),
                                        currentState.getSelectedType());
                            }
                        }
                    });
        }

        // Set default background color for range buttons
        for (JButton b : rangeButtons) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
            b.setFocusPainted(false); // remove outline when focused
        }
        setActive(dayButton, rangeButtons); // Default selected

        for (JButton button : rangeButtons) {
            button.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            setActive(button, rangeButtons);
                            final GraphState currentState = gvm.getState();
                            gc.execute(
                                    button.getText(),
                                    currentState.getSelectedType());
                        }
                    });
        }

        // type selector buttons
        JPanel typeSelectorContainer = new JPanel();
        typeSelectorContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
        typeSelectorContainer.setBackground(Color.WHITE);
        JButton incomeButton = new JButton("Income");
        JButton expenseButton = new JButton("Expense");
        typeSelectorContainer.add(expenseButton);
        typeSelectorContainer.add(incomeButton);
        topContainer.add(typeSelectorContainer);

        /* add listeners for type buttons */
        JButton[] typeButtons = { incomeButton, expenseButton };

        for (JButton button : typeButtons) {
            final JButton b = button;
            button.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (evt.getSource().equals(b)) {
                                final GraphState currentState = gvm.getState();
                                gc.execute(
                                        currentState.getSelectedRange(),
                                        b.getText());
                            }
                        }
                    });
        }

        // Set default background color for type buttons
        for (JButton b : typeButtons) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
            b.setFocusPainted(false); // remove outline when focused
        }
        setActive(expenseButton, typeButtons); // Default selected

        for (JButton button : typeButtons) {
            button.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            setActive(button, typeButtons);
                            final GraphState currentState = gvm.getState();
                            gc.execute(
                                    currentState.getSelectedRange(),
                                    button.getText());
                        }
                    });
        }

        // create bar graph
        JFreeChart barChart = createBarChart();
        barChart.getPlot().setBackgroundPaint(Color.WHITE);
        leftGraphContainer = new ChartPanel(barChart);
        bottomContainer.add(leftGraphContainer);

        // create pie graph
        JFreeChart pieChart = createPieChart();
        pieChart.getPlot().setBackgroundPaint(Color.WHITE);
        rightGraphContainer = new ChartPanel(pieChart);
        bottomContainer.add(rightGraphContainer);
    }

    @SuppressWarnings("unchecked")
    private JFreeChart createPieChart() {

        pieDataset = new DefaultPieDataset<>();

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Category",
                pieDataset,
                true,
                true,
                false);

        // Add value labels to the pie chart
        org.jfree.chart.plot.PiePlot<String> plot = (org.jfree.chart.plot.PiePlot<String>) pieChart.getPlot();
        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0}\n${1}", java.text.NumberFormat.getInstance(), new java.text.DecimalFormat("0.00")));

        return pieChart;
    }

    private JFreeChart createBarChart() {
        barDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart(
                "Date",
                "",
                "",
                barDataset);

        // TODO: Configure x-axis to reduce overlapping labels

        return barChart;
    }

    public void setGraphController(GraphController gc) {
        this.gc = gc;
    }

    /**
     * react to a bottom click
     * 
     * @param evt event
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("clicked " + evt.getActionCommand());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final GraphState state = (GraphState) evt.getNewValue();
        updateGraph(state);
    }

    private void updateGraph(GraphState state) {
        // clear previous values
        barDataset.clear();
        pieDataset.clear();

        // update bar dataset (use bar map)
        Map<Integer, Float> bar = state.getBar();
        if (bar != null) {
            for (Map.Entry<Integer, Float> entry : bar.entrySet()) {
                Integer colKey = entry.getKey();
                Float val = entry.getValue();
                if (colKey != null && val != null) {
                    barDataset.addValue(val, state.getSelectedType(), colKey);
                }
            }
        }

        // update pie dataset
        Map<String, Float> data = state.getPie();
        // add each category to pie chart
        if (data != null) {
            for (Map.Entry<String, Float> entry : data.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    pieDataset.setValue(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * helper to set active ttons
     * 
     * @param btn  active button
     * @param btns all buttons
     */
    private void setActive(JButton btn, JButton[] btns) {
        for (JButton b : btns) {
            b.setBackground(Color.WHITE);
            b.setForeground(Color.BLACK);
        }
        btn.setBackground(new Color(50, 50, 50));
        btn.setForeground(Color.WHITE);
    }
}
