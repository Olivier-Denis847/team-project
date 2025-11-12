package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GraphPanel {
    private JFrame graphFrame;
    private JPanel lineGraphContainer;
    private JPanel pieGraphContainer;
    private JButton[] rangeSelectorButtons;
    private JButton[] typeSelectorButtons;

    public GraphPanel() {
        JFrame graphFrame = new JFrame("Trend Graph");
        graphFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        graphFrame.setSize(800, 600);
        graphFrame.setLocationRelativeTo(null);
        graphFrame.getContentPane().setLayout(new GridLayout(1, 2));
        this.graphFrame = graphFrame;

        JPanel lineGraphContainer = new JPanel();
        JPanel pieGraphContainer = new JPanel();
        lineGraphContainer.setLayout(new BoxLayout(lineGraphContainer, BoxLayout.Y_AXIS));
        pieGraphContainer.setLayout(new BoxLayout(pieGraphContainer, BoxLayout.Y_AXIS));
        this.lineGraphContainer = lineGraphContainer;
        this.pieGraphContainer = pieGraphContainer;

        graphFrame.add(lineGraphContainer);
        graphFrame.add(pieGraphContainer);

        /* line graph container */
        JPanel rangeSelectorContainer = new JPanel();
        rangeSelectorContainer.setLayout(new BoxLayout(rangeSelectorContainer, BoxLayout.X_AXIS));
        JButton dayButton = new JButton("Day");
        JButton monthButton = new JButton("Month");
        JButton yearButton = new JButton("Year");
        // TODO: add actionlistner
        rangeSelectorContainer.add(dayButton);
        rangeSelectorContainer.add(monthButton);
        rangeSelectorContainer.add(yearButton);
        lineGraphContainer.add(rangeSelectorContainer);
        lineGraphContainer.setBackground(Color.CYAN);

        this.rangeSelectorButtons = new JButton[]{dayButton, monthButton, yearButton};

        /* pie graph container */
        JPanel typeSelectorContainer = new JPanel();
        typeSelectorContainer.setLayout(new BoxLayout(typeSelectorContainer, BoxLayout.X_AXIS));
        JButton incomeButton = new JButton("Income");
        JButton expenseButton = new JButton("Expense");
        // TODO: add actionlistener
        typeSelectorContainer.add(incomeButton);
        typeSelectorContainer.add(expenseButton);
        pieGraphContainer.add(typeSelectorContainer);

        this.typeSelectorButtons = new JButton[]{incomeButton, expenseButton};

        /* show frame */
        graphFrame.setVisible(true);
    }
}
