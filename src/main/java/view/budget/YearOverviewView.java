package view.budget;

import javax.swing.*;
import java.awt.*;

public class YearOverviewView extends JPanel {

    private final Runnable onBackToMenu;

    public YearOverviewView(Runnable onBackToMenu) {

        this.onBackToMenu = onBackToMenu;

        setLayout(new BorderLayout());

        // TOP: Back button row
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);
    }
}
