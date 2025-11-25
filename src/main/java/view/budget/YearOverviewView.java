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

    private final BudgetDataAccessInterface dataAccess;
    private final DefaultTableModel tableModel;
    private int currentYear;

    public YearOverviewView(BudgetDataAccessInterface dataAccess, Runnable onBackToMenu, Consumer<String> onSelectMonth) {
        this.dataAccess = dataAccess;
        setLayout(new BorderLayout());

        // Back button
        JButton backButton = new JButton("← BACK");
        backButton.addActionListener(e -> {if (onBackToMenu != null) onBackToMenu.run();});
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // Start from current year
        currentYear = java.time.Year.now().getValue();

        JButton prevYear = new JButton("←");
        JButton nextYear = new JButton("→");
        JLabel yearLabel = new JLabel(String.valueOf(currentYear));
        yearLabel.setFont(yearLabel.getFont().deriveFont(Font.BOLD, 18f));

        JPanel yearPanel = new JPanel();
        yearPanel.add(prevYear);
        yearPanel.add(yearLabel);
        yearPanel.add(nextYear);

        // Table and model setup
        String[] cols = {"Month", "Limit", "Spent", "Remaining", "Status", "Updated"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // Striped background
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0
                            ? new Color(245, 245, 245)
                            : Color.WHITE);
                }

                // Status colour in column 4
                if (!isRowSelected(row)) {
                    if (column == 4) {
                        String status = String.valueOf(getValueAt(row, column));
                        Color color;
                        if ("On track".equals(status)) {
                            color = new Color(4, 201, 4);
                        }
                        else if ("Budget hit".equals(status)) {
                            color = new Color(253, 218, 13);
                        }
                        else if ("Over budget".equals(status)) {
                            color = Color.RED;
                        }
                        else {
                            color = Color.BLACK;
                        }
                        c.setForeground(color);
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        };
        // Slightly wider column to fit content
        table.getColumnModel().getColumn(5).setMinWidth(90);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        // Custom sorting for each column
        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();

        // Sort month column by calendar order, not alphabetical
        sorter.setComparator(0, (a, b) -> {
            String order = "January February March April May June July August September October November December";
            return order.indexOf(a.toString()) - order.indexOf(b.toString());
        });

        // Double-click row to jump to that month in CheckBudgetView
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                // double-click OR single-click? (choose one)
                if (e.getClickCount() == 2) {   // double-click row
                    int row = table.getSelectedRow();
                    if (row < 0) return;

                    // Convert view row -> model row
                    int modelRow = table.convertRowIndexToModel(row);

                    String monthName = tableModel.getValueAt(modelRow, 0).toString();
                    String year = String.valueOf(currentYear);

                    // Convert month name -> month number
                    String[] months = {
                            "January","February","March","April","May","June",
                            "July","August","September","October","November","December"
                    };

                    int monthNumber = -1;
                    for (int i = 0; i < 12; i++) {
                        if (months[i].equals(monthName)) {
                            monthNumber = i + 1;
                            break;
                        }
                    }

                    // Build month key "MM-YYYY" and notify callback
                    if (monthNumber > 0) {
                        String monthKey = String.format("%02d-%s", monthNumber, year);

                        // ⬅️ Trigger callback
                        if (onSelectMonth != null) onSelectMonth.accept(monthKey);
                    }
                }
            }
        });

        // Order for money columns (empty treated as least)
        Comparator<String> moneyComparator = (s1, s2) -> {
            try {
                float f1;
                if (s1.equals("—")) {
                    f1 = Float.NEGATIVE_INFINITY;
                }
                else {
                    f1 = Float.parseFloat(s1.replace("$", ""));
                }

                float f2;
                if (s2.equals("—")) {
                    f2 = Float.NEGATIVE_INFINITY;
                }
                else {
                    f2 = Float.parseFloat(s2.replace("$", ""));
                }
                return Float.compare(f1, f2);
            } catch (Exception e) {
                return 0;
            }
        };

        // Columns: 1 = Limit, 2 = Spent, 3 = Remaining
        sorter.setComparator(1, moneyComparator);
        sorter.setComparator(2, moneyComparator);
        sorter.setComparator(3, moneyComparator);

        // Status comparator
        sorter.setComparator(4, (a, b) -> {
            String s1 = a.toString(), s2 = b.toString();
            int v1 = statusRank(s1), v2 = statusRank(s2);
            return Integer.compare(v1, v2);
        });

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(yearPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Year navigation
        prevYear.addActionListener(e -> {
            if (currentYear > MIN_YEAR) {
                currentYear--;
                yearLabel.setText(String.valueOf(currentYear));
                updateYearButtons(prevYear, nextYear);
                populateYear(currentYear);
            }
        });
        nextYear.addActionListener(e -> {
            if (currentYear < MAX_YEAR) {
                currentYear++;
                yearLabel.setText(String.valueOf(currentYear));
                updateYearButtons(prevYear, nextYear);
                populateYear(currentYear);
            }
        });

        // Initial state
        updateYearButtons(prevYear, nextYear);
        populateYear(currentYear);

        // Continuously refresh DAO everytime view is shown
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                populateYear(currentYear);
            }
        });
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
