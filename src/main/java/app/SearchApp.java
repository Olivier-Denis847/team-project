package app;

import data_access.FinanceDataAccess;

import javax.swing.*;
import java.awt.*;

public class SearchApp {
    private SearchApp(){
        throw new IllegalStateException("Utility class");
    }

    public static void start(FinanceDataAccess dataAccess) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Search");

            // main panel
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel label = new JLabel("Enter view: Budget, Graph, Optimize");
            JTextField inputField = new JTextField(15);

            JLabel errorLabel = new JLabel(" ");
            errorLabel.setForeground(Color.RED);
            errorLabel.setFont(errorLabel.getFont().deriveFont(Font.PLAIN, 14f));

            JPanel errorRow = new JPanel();
            errorRow.setLayout(new BoxLayout(errorRow, BoxLayout.X_AXIS));
            errorRow.add(Box.createHorizontalGlue());
            errorRow.add(errorLabel);
            errorRow.add(Box.createHorizontalGlue());

            JButton openButton = new JButton("Open");

            JPanel inputRow = new JPanel(new BorderLayout(5, 0));
            inputRow.add(inputField, BorderLayout.CENTER);
            inputRow.add(openButton, BorderLayout.EAST);

            panel.add(label);
            panel.add(Box.createVerticalStrut(8));
            panel.add(inputRow);
            panel.add(Box.createVerticalStrut(5));
            panel.add(errorRow);

            Runnable handleSearch = () -> {
                String text = inputField.getText().trim().toLowerCase();

                if (text.isEmpty()) {
                    errorLabel.setText("Please enter: budget, graph, or optimize.");
                    return;
                }

                switch (text) {
                    case "budget" -> {
                        BudgetApp.start(dataAccess);
                        frame.dispose();
                    }
                    case "graph" -> {
                        GraphApp.start(dataAccess);
                        frame.dispose();
                    }
                    case "optimize" -> {
                        OptimizeApp.start();
                        frame.dispose();
                    }
                    default ->
                        errorLabel.setText("Unknown view: \"" + text + "\"");
                }
            };

            openButton.addActionListener(e -> handleSearch.run());
            inputField.addActionListener(e -> handleSearch.run());

            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}