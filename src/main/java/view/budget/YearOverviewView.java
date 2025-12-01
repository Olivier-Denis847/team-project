package view.budget;

import entity.Budget;
import use_case.budget.BudgetDataAccessInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.util.function.Consumer;

public class YearOverviewView extends JPanel {

    private static final int MIN_YEAR = 2006;
    private static final int MAX_YEAR = 2030;

    private final transient BudgetDataAccessInterface dataAccess;
    private DefaultTableModel tableModel;
    private int currentYear;
    private static final String[] MONTH_NAMES = {
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
    };


    public YearOverviewView(BudgetDataAccessInterface dataAccess,
                            Runnable onBackToMenu,
                            Consumer<String> onSelectMonth) {
        this.dataAccess = dataAccess;
        setLayout(new BorderLayout());

        // Back button / top bar
        add(buildTopPanel(onBackToMenu), BorderLayout.NORTH);

        // Start from current year
        currentYear = java.time.Year.now().getValue();

        JButton prevYear = new JButton("←");
        JButton nextYear = new JButton("→");
        JLabel yearLabel = createYearLabel();

        JPanel yearPanel = buildYearPanel(prevYear, yearLabel, nextYear);

        JTable table = createBudgetTable();
        configureTableSorting(table);
        configureTableDoubleClick(table, onSelectMonth);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(yearPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        configureYearNavigation(prevYear, nextYear, yearLabel);

        // Initial state
        updateYearButtons(prevYear, nextYear);
        populateYear(currentYear);

        // Refresh DAO every time view is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                populateYear(currentYear);
            }
        });
    }

    private JPanel buildTopPanel(Runnable onBackToMenu) {
        JButton backButton = new JButton("← BACK");
        backButton.addActionListener(e -> {
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        return topPanel;
    }

    private JLabel createYearLabel() {
        JLabel yearLabel = new JLabel(String.valueOf(currentYear));
        yearLabel.setFont(yearLabel.getFont().deriveFont(Font.BOLD, 18f));
        return yearLabel;
    }

    private JPanel buildYearPanel(JButton prevYear, JLabel yearLabel, JButton nextYear) {
        JPanel yearPanel = new JPanel();
        yearPanel.add(prevYear);
        yearPanel.add(yearLabel);
        yearPanel.add(nextYear);
        return yearPanel;
    }

    private JTable createBudgetTable() {
        String[] cols = {"Month", "Limit", "Spent", "Remaining", "Status", "Updated"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                applyStripedBackground(this, c, row);
                applyStatusColor(this, c, row, column);
                return c;
            }
        };

        table.getColumnModel().getColumn(5).setMinWidth(90);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        return table;
    }

    private void applyStripedBackground(JTable table, Component c, int row) {
        if (!table.isRowSelected(row)) {
            c.setBackground(row % 2 == 0
                    ? new Color(245, 245, 245)
                    : Color.WHITE);
        }
    }

    private void applyStatusColor(JTable table, Component c, int row, int column) {
        if (table.isRowSelected(row)) {
            return;
        }
        if (column != 4) {
            c.setForeground(Color.BLACK);
            return;
        }

        String status = String.valueOf(table.getValueAt(row, column));
        Color color;
        if ("On track".equals(status)) {
            color = new Color(4, 201, 4);
        } else if ("Budget hit".equals(status)) {
            color = new Color(253, 218, 13);
        } else if ("Over budget".equals(status)) {
            color = Color.RED;
        } else {
            color = Color.BLACK;
        }
        c.setForeground(color);
    }

    private void configureTableSorting(JTable table) {
        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();

        // Month column by calendar order
        sorter.setComparator(0, (a, b) -> {
            int m1 = monthNameToNumber(a.toString());
            int m2 = monthNameToNumber(b.toString());
            return Integer.compare(m1, m2);
        });

        Comparator<String> moneyComparator = this::compareMoneyStrings;
        sorter.setComparator(1, moneyComparator);
        sorter.setComparator(2, moneyComparator);
        sorter.setComparator(3, moneyComparator);

        sorter.setComparator(4, (a, b) -> {
            String s1 = a.toString();
            String s2 = b.toString();
            int v1 = statusRank(s1);
            int v2 = statusRank(s2);
            return Integer.compare(v1, v2);
        });
    }

    private int compareMoneyStrings(String s1, String s2) {
        try {
            float f1 = parseMoneyOrDefault(s1);
            float f2 = parseMoneyOrDefault(s2);
            return Float.compare(f1, f2);
        } catch (Exception e) {
            return 0;
        }
    }

    private float parseMoneyOrDefault(String s) {
        if ("—".equals(s)) {
            return Float.NEGATIVE_INFINITY;
        }
        return Float.parseFloat(s.replace("$", ""));
    }

    private int monthNameToNumber(String monthName) {
        for (int i = 0; i < MONTH_NAMES.length; i++) {
            if (MONTH_NAMES[i].equals(monthName)) {
                return i + 1;
            }
        }
        return -1;
    }

    private void configureTableDoubleClick(JTable table, Consumer<String> onSelectMonth) {
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2) {
                    return;
                }

                int row = table.getSelectedRow();
                if (row < 0) {
                    return;
                }

                int modelRow = table.convertRowIndexToModel(row);
                String monthName = tableModel.getValueAt(modelRow, 0).toString();
                int monthNumber = monthNameToNumber(monthName);
                if (monthNumber <= 0) {
                    return;
                }

                String year = String.valueOf(currentYear);
                String monthKey = String.format("%02d-%s", monthNumber, year);

                if (onSelectMonth != null) {
                    onSelectMonth.accept(monthKey);
                }
            }
        });
    }

    private void configureYearNavigation(JButton prevYear, JButton nextYear, JLabel yearLabel) {
        prevYear.addActionListener(e -> changeYear(-1, prevYear, nextYear, yearLabel));
        nextYear.addActionListener(e -> changeYear(1, prevYear, nextYear, yearLabel));
    }

    private void changeYear(int direction, JButton prevYear, JButton nextYear, JLabel yearLabel) {
        int newYear = currentYear + direction;
        if (newYear < MIN_YEAR || newYear > MAX_YEAR) {
            return;
        }

        currentYear = newYear;
        yearLabel.setText(String.valueOf(currentYear));
        updateYearButtons(prevYear, nextYear);
        populateYear(currentYear);
    }

    // Enable/disable prev/next buttons at year bounds
    private void updateYearButtons(JButton prevButton, JButton nextButton) {
        prevButton.setEnabled(currentYear > MIN_YEAR);
        nextButton.setEnabled(currentYear < MAX_YEAR);
    }

    // Fill table with all 12 months for a given year
    private void populateYear(int year) {
        tableModel.setRowCount(0);
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };

        for (int i = 0; i < 12; i++) {
            String monthName = months[i];
            String monthKey = String.format("%02d-%d", i + 1, year);
            Budget b = dataAccess.getBudgetForMonth(monthKey);

            if (b == null) {
                tableModel.addRow(new Object[]{
                        monthName,
                        "—", "—", "—",
                        "No budget",
                        "—"
                });
            }
            else {
                String limit = formatMoney(b.getLimit());
                String spent = formatMoney(b.getTotalSpent());
                String remaining = formatMoney(b.getRemaining());
                String status = b.getStatus();
                String updated = formatUpdated(b.getLastUpdated());

                tableModel.addRow(new Object[]{
                        monthName,
                        limit,
                        spent,
                        remaining,
                        status,
                        updated
                });
            }
        }
    }

    // Ensure dollar sign and negative values are properly formatted (same as CheckBudgetView)
    private String formatMoney(float amount) {
        if (amount < 0) {
            return "-$" + String.format("%.2f", Math.abs(amount));
        }
        return "$" + String.format("%.2f", amount);
    }

    // Removes seconds and timezone for timestamp
    private String formatUpdated(String raw) {
        if (raw == null || raw.isBlank()) return "—";
        String[] p = raw.split(" ");
        if (p.length < 2) return raw;
        String t = p[1];                 // "HH:mm" or "HH:mm:ss"
        if (t.length() > 5) t = t.substring(0, 5);
        return p[0] + " " + t;           // "yyyy-MM-dd HH:mm"
    }

    // Map status strings to sort order
    private int statusRank(String s) {
        return switch (s) {
            case "On track" -> 0;
            case "Budget hit" -> 1;
            case "Over budget" -> 2;
            default -> 3;
        };
    }
}
