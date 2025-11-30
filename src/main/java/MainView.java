
import app.GraphApp;
import app.TransactionApp;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;


    public MainView(MainInputData data) {
        super("Finance UI");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel navPanel = createNavPanel();

        add(navPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);

        JPanel summaryPanel = createSpendingPanel(data);

        centerPanel.add(summaryPanel);

        JPanel listsPanel = createListPanel(data);

        centerPanel.add(listsPanel);

        JPanel bottomPanel = createButtonsPanel();

        centerPanel.add(bottomPanel);
    }

    private JPanel createButtonsPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        JButton addTransaction = new JButton("Add Transaction");
        addTransaction.addActionListener(e -> {
            TransactionApp.ShowTransactionApp();
        });



        addTransaction.setPreferredSize(new Dimension(280, 100));

        bottomPanel.add(addTransaction);
        return bottomPanel;
    }

    private JPanel createListPanel(MainInputData data) {
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        listsPanel.add(makeListPanel("Expenses", data.getExpenses()));
        listsPanel.add(makeListPanel("Income", data.getIncomes()));
        return listsPanel;
    }

    private static JPanel createSpendingPanel(MainInputData data) {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
        summaryPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel l1 = new JLabel("Remaining: $"
                + String.format("%.2f", (data.getMoney() - data.getSpent())));
        l1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        summaryPanel.add(l1);

        JLabel l2 = new JLabel("Spent: $"
                + String.format("%.2f", data.getSpent()));
        l2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        summaryPanel.add(l2);

        l1.setHorizontalAlignment(SwingConstants.CENTER);
        l2.setHorizontalAlignment(SwingConstants.CENTER);

        l2.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        return summaryPanel;
    }

    private static JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        navPanel.add(Box.createVerticalGlue());
        navPanel.setBackground(new Color(230, 230, 230));
        navPanel.setOpaque(true);

        String[] navItems = {"Search", "Budget", "Graph", "Optimize"};
        addButtons(navPanel, navItems);

        navPanel.add(Box.createVerticalGlue());
        return navPanel;
    }

    private static void addButtons(JPanel navPanel, String[] navItems) {
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(140, 45));
            btn.setPreferredSize(new Dimension(140, 45));
            navPanel.add(btn);
            navPanel.add(Box.createRigidArea(new Dimension(0, 20)));

            btn.addActionListener(e -> {
                switch (item) {
                    case "Search": {
                        System.out.println("Search use case");
                        break;
                    }
                    case "Budget": {
                        System.out.println("Budget use case");
                        break;
                    }
                    case "Graph": {
                        GraphApp.showGraphView();
                        break;
                    }
                    case "Optimize": {
                        System.out.println("Optimize use case");
                        break;
                    }
                    default: {
                        System.out.println("Unknown item: " + item);
                    }
                }
                //ToDo Call the respective use case
            });
        }
    }

    // Creates scrolling lists for Expenses / Income
    private JPanel makeListPanel(String title, String[] items) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel listItems = new JPanel();
        listItems.setLayout(new BoxLayout(listItems, BoxLayout.Y_AXIS));

        makeLabel(items, listItems);

        JScrollPane scrollPane = new JScrollPane(listItems);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private static void makeLabel(String[] items, JPanel listItems) {
        for (int i = 0; i < items.length; i++) {
            JLabel label = new JLabel(items[i]);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

            if (i%2 == 0)
                label.setBackground(new Color(245, 245, 245));
            else
                label.setBackground(new Color(230, 230, 230));
            label.setOpaque(true);

            label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            //ToDo Call the add label use case
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    System.out.println("Clicked: " + label.getText());
                }
            });
            listItems.add(label);
        }
    }

    public static void main(String[] args) {
        //ToDo Get incomes/expenses from a database


        String[] expenses = {"Exp 1", "Exp 2", "Exp 3"};
        String[] incomes = {"Inc 1", "Inc 2", "Inc 3"};
        float money = 1000.25F;
        float spent = 750.10F;
        MainInputData data = new MainInputData(expenses, incomes, money, spent);
        MainView ui = new MainView(data);
        ui.setVisible(true);
    }
}
