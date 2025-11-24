package Brandon.view;

import Brandon.entities.Budget;
import Brandon.useCase.BudgetDataAccessInterface;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class YearOverviewView extends JPanel {

    private static final int MIN_YEAR = 2006;
    private static final int MAX_YEAR = 2030;

    private final BudgetDataAccessInterface dataAccess;
    private final DefaultTableModel tableModel;
    private int currentYear;

    public YearOverviewView(BudgetDataAccessInterface dataAccess,
                            Runnable onBackToMenu) {
        this.dataAccess = dataAccess;
        setLayout(new BorderLayout());

        JButton backButton = new JButton("← BACK");
        backButton.addActionListener(e -> {
            if (onBackToMenu != null) onBackToMenu.run();
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        currentYear = java.time.Year.now().getValue();

        JButton prevYear = new JButton("←");
        JButton nextYear = new JButton("→");
        JLabel yearLabel = new JLabel(String.valueOf(currentYear));
        yearLabel.setFont(yearLabel.getFont().deriveFont(Font.BOLD, 18f));

        JPanel yearPanel = new JPanel();
        yearPanel.add(prevYear);
        yearPanel.add(yearLabel);
        yearPanel.add(nextYear);

        String[] cols = {"Month", "Limit", "Spent", "Remaining", "Status", "Updated"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // striped background
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0
                            ? new Color(245, 245, 245)
                            : Color.WHITE);
                }

                // status colour in column 4 only, reset others to black
                if (!isRowSelected(row)) {
                    if (column == 4) { // "Status" column index
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
        table.getColumnModel().getColumn(5).setMinWidth(90);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();

        sorter.setComparator(0, (a, b) -> {
            String order = "January February March April May June July August September October November December";
            return order.indexOf(a.toString()) - order.indexOf(b.toString());
        });

        // Numeric comparator helper
        Comparator<String> moneyComparator = (s1, s2) -> {
            try {
                float f1 = Float.parseFloat(s1.replace("$",""));
                float f2 = Float.parseFloat(s2.replace("$",""));
                return Float.compare(f1, f2);
            } catch (Exception e) {
                return 0;
            }
        };

// Columns: 1 = Limit, 2 = Spent, 3 = Remaining
        sorter.setComparator(1, moneyComparator);
        sorter.setComparator(2, moneyComparator);
        sorter.setComparator(3, moneyComparator);

// Status comparator (optional)
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

    private void updateYearButtons(JButton prev, JButton next) {
        prev.setEnabled(currentYear > MIN_YEAR);
        next.setEnabled(currentYear < MAX_YEAR);
    }

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
            } else {
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

    private String formatMoney(float amount) {
        if (amount < 0) {
            return "-$" + String.format("%.2f", Math.abs(amount));
        }
        return "$" + String.format("%.2f", amount);
    }

    private String formatUpdated(String raw) {
        if (raw == null || raw.isBlank()) return "—";
        String[] p = raw.split(" ");
        if (p.length < 2) return raw;
        String t = p[1];                 // "HH:mm" or "HH:mm:ss"
        if (t.length() > 5) t = t.substring(0, 5);
        return p[0] + " " + t;           // "yyyy-MM-dd HH:mm"
    }

    private int statusRank(String s) {
        return switch (s) {
            case "On track" -> 0;
            case "Budget hit" -> 1;
            case "Over budget" -> 2;
            default -> 3;
        };
    }
}
