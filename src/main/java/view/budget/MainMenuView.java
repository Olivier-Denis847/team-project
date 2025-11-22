package view.budget;

import javax.swing.*;
import java.awt.*;

public class MainMenuView extends JPanel {

    public MainMenuView(Runnable onAddBudget,
                        Runnable onCheckBudget,
                        Runnable onFullYear) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Budget Manager");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Choose an option:");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JButton addBudgetButton = new JButton("Add / Edit Budget");
        JButton checkBudgetButton = new JButton("Check Budget");
        JButton fullYearButton = new JButton("Full Year Budgets");

        addBudgetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBudgetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullYearButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(220, 40);
        addBudgetButton.setPreferredSize(buttonSize);
        checkBudgetButton.setPreferredSize(buttonSize);
        fullYearButton.setPreferredSize(buttonSize);

        addBudgetButton.addActionListener(e -> {
            if (onAddBudget != null) onAddBudget.run();
        });

        checkBudgetButton.addActionListener(e -> {
            if (onCheckBudget != null) onCheckBudget.run();
        });

        fullYearButton.addActionListener(e -> {
            if (onFullYear != null) onFullYear.run();
        });

        add(title);
        add(subtitle);
        add(Box.createVerticalStrut(10));
        add(addBudgetButton);
        add(Box.createVerticalStrut(10));
        add(checkBudgetButton);
        add(Box.createVerticalStrut(10));
        add(fullYearButton);
    }
}
