package view.budget;

import javax.swing.*;
import java.awt.*;

public class MainMenuView extends JPanel {

    // Main menu screen displaying all 3 navigation options
    public MainMenuView(Runnable onAddBudget, Runnable onCheckBudget, Runnable onFullYear) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Budget Manager");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Select an option:");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        Dimension buttonSize = new Dimension(220, 40);

        JButton addBudgetButton = createMenuButton("Add / Edit Budget", buttonSize, onAddBudget);
        JButton checkBudgetButton = createMenuButton("Check Budget", buttonSize, onCheckBudget);
        JButton fullYearButton = createMenuButton("Year Overview", buttonSize, onFullYear);

        // Center everything vertically a bit more nicely
        add(Box.createVerticalGlue());
        add(title);
        add(subtitle);
        add(addBudgetButton);
        add(Box.createVerticalStrut(10));
        add(checkBudgetButton);
        add(Box.createVerticalStrut(10));
        add(fullYearButton);
        add(Box.createVerticalGlue());
    }

    // Helper to make consistent, centered buttons
    private JButton createMenuButton(String text, Dimension size, Runnable action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setMinimumSize(size);
        button.addActionListener(e -> {
            if (action != null) action.run();
        });
        return button;
    }
}
