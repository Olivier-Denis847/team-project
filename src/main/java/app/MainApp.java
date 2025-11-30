package app;

import data_access.FinanceDataAccess;
import entity.Transaction;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainApp extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private final FinanceDataAccess dataAccess;
    private JPanel centerPanel;

    public MainApp(FinanceDataAccess dataAccess) {
        super("Finance UI");
        this.dataAccess = dataAccess;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel navPanel = createNavPanel();

        add(navPanel, BorderLayout.WEST);

        centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        add(centerPanel, BorderLayout.CENTER);

        refreshUI();
    }

    /**
     * Refresh the UI to display current transaction data
     */
    public void refreshUI() {
        centerPanel.removeAll();

        JPanel summaryPanel = createSpendingPanel(dataAccess);
        centerPanel.add(summaryPanel);

        JPanel listsPanel = createListPanel(dataAccess);
        centerPanel.add(listsPanel);

        JPanel bottomPanel = createButtonsPanel(dataAccess, this);
        centerPanel.add(bottomPanel);

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private static JPanel createButtonsPanel(FinanceDataAccess dataAccess, MainApp mainApp) {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));

        // Call the add transaction use case
        JButton addExp = new JButton("Add Expense");
        addExp.addActionListener(e -> {
            TransactionApp.start(dataAccess, "Expense", mainApp);
        });

        JButton addInc = new JButton("Add Income");
        addInc.addActionListener(e -> {
            TransactionApp.start(dataAccess, "Income", mainApp);
        });

        addExp.setPreferredSize(new Dimension(200, 50));
        addInc.setPreferredSize(new Dimension(200, 50));

        bottomPanel.add(addExp);
        bottomPanel.add(addInc);
        return bottomPanel;
    }

    private JPanel createListPanel(FinanceDataAccess dataAccess) {
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        listsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        listsPanel.setPreferredSize(new Dimension(800, 400));

        List<Transaction> expenses = dataAccess.getExpenses();
        List<Transaction> incomes = dataAccess.getIncomes();

        listsPanel.add(makeListPanel("Expenses", expenses));
        listsPanel.add(makeListPanel("Income", incomes));
        return listsPanel;
    }

    private static JPanel createSpendingPanel(FinanceDataAccess dataAccess) {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 1));
        summaryPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        summaryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Calculate totals from transactions
        float totalIncome = calculateTotal(dataAccess.getIncomes());
        float totalExpenses = calculateTotal(dataAccess.getExpenses());

        JLabel l1 = new JLabel("Remaining: $"
                + String.format("%.2f", (totalIncome - totalExpenses)));
        l1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        summaryPanel.add(l1);

        JLabel l2 = new JLabel("Spent: $"
                + String.format("%.2f", totalExpenses));
        l2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        summaryPanel.add(l2);

        l1.setHorizontalAlignment(SwingConstants.CENTER);
        l2.setHorizontalAlignment(SwingConstants.CENTER);

        l2.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        return summaryPanel;
    }

    private JPanel createNavPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 20));

        navPanel.add(Box.createVerticalGlue());
        navPanel.setBackground(new Color(230, 230, 230));
        navPanel.setOpaque(true);

        String[] navItems = { "Search", "Budget", "Graph", "Optimize" };
        addButtons(navPanel, navItems);

        navPanel.add(Box.createVerticalGlue());
        return navPanel;
    }

    private void addButtons(JPanel navPanel, String[] navItems) {
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
                        SearchApp.start(dataAccess);
                        break;
                    }
                    case "Budget": {
                        BudgetApp.start(dataAccess);
                        break;
                    }
                    case "Graph": {
                        GraphApp.start(dataAccess);
                        break;
                    }
                    case "Optimize": {
                        OptimizeApp.start();
                        break;
                    }
                }
            });
        }
    }

    // Creates scrolling lists for Expenses / Income
    private JPanel makeListPanel(String title, List<Transaction> transactions) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel listItems = new JPanel();
        listItems.setLayout(new BoxLayout(listItems, BoxLayout.Y_AXIS));

        makeTransactionLabels(transactions, listItems, dataAccess, this);

        JScrollPane scrollPane = new JScrollPane(listItems);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Helper method to calculate total amount from a list of transactions.
     *
     * @param transactions list of transactions
     * @return total amount
     */
    private static float calculateTotal(List<Transaction> transactions) {
        float total = 0;
        for (Transaction t : transactions) {
            total += t.getAmount();
        }
        return total;
    }

    private static void makeTransactionLabels(List<Transaction> transactions, JPanel listItems,
            FinanceDataAccess dataAccess, MainApp mainApp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        // Display newest transactions first (reverse order)
        List<Transaction> reversedTransactions = new ArrayList<>(transactions);
        java.util.Collections.reverse(reversedTransactions);

        int index = 0;
        for (Transaction transaction : reversedTransactions) {
            String dateStr = transaction.getDate() != null ? dateFormat.format(transaction.getDate()) : "No date";
            String note = transaction.getNote() != null ? transaction.getNote() : "No note";
            String displayText = String.format("$%.2f - %s (%s)", transaction.getAmount(), note, dateStr);

            JLabel label = new JLabel(displayText);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

            if (index % 2 == 0)
                label.setBackground(new Color(245, 245, 245));
            else
                label.setBackground(new Color(230, 230, 230));
            label.setOpaque(true);

            label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
            label.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Make transaction clickable to open label assignment dialog
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    openLabelAssignmentDialog(transaction, dataAccess, mainApp);
                }
            });

            listItems.add(label);
            index++;
        }
    }

    private static void openLabelAssignmentDialog(Transaction transaction, FinanceDataAccess dataAccess,
            MainApp mainApp) {
        SwingUtilities.invokeLater(() -> {
            use_case.label.LabelUserCase labelUserCase = new use_case.label.LabelUserCaseImp(dataAccess,
                    new data_access.ALEDataAccess());
            interface_adapter.label.LabelController controller = new interface_adapter.label.LabelController(
                    labelUserCase);

            view.label.AssignLabelView assignView = new view.label.AssignLabelView(
                    controller,
                    transaction,
                    1, // Default user ID
                    () -> mainApp.refreshUI(),
                    dataAccess);
            assignView.setVisible(true);
        });
    }

    public static void main(String[] args) {
        // Create the central data access object
        FinanceDataAccess dataAccess = new FinanceDataAccess();

        // Launch the UI with the data access
        MainApp ui = new MainApp(dataAccess);
        ui.setVisible(true);
    }
}