package Brandon.view;

import Brandon.entities.Budget;
import Brandon.useCase.BudgetDataAccessInterface;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.function.Consumer;

public class CheckBudgetView extends JPanel {

    private final BudgetDataAccessInterface dataAccess;

    // Month & year dropdowns
    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;

    private final JButton prevButton = new JButton("←");
    private final JButton nextButton = new JButton("→");
    private static final int MIN_YEAR = 2006;
    private static final int MAX_YEAR = 2030;

    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel limitLabel = new JLabel("Limit: ");
    private final JLabel spentLabel = new JLabel("Total spent: ");
    private final JLabel remainingLabel = new JLabel("Remaining: ");

    private final JLabel statusValueLabel = new JLabel("");

    // Extra notes + char limit
    private final JTextArea notesArea = new JTextArea();
    private final JLabel charCountLabel = new JLabel("0 / 250");
    private final JLabel lastUpdatedLabel = new JLabel(" ");
    private static final int NOTES_MAX_CHARS = 250;
    private static final String NOTES_PLACEHOLDER = "Extra notes";
    private static final String NOTES_LOCKED_PLACEHOLDER = "Please set budget first!";
    private String currentMonthKey = null;
    private boolean suppressNoteEvents = false;

    public CheckBudgetView(BudgetDataAccessInterface dataAccess,
                           Runnable onBackToMenu,
                           Consumer<String> onAddBudgetForMonth) {
        this.dataAccess = dataAccess;

        // --- month & year models ---
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        monthCombo = new JComboBox<>(months);

        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int y = MIN_YEAR; y <= MAX_YEAR; y++) {
            yearModel.addElement(y);
        }
        yearCombo = new JComboBox<>(yearModel);

        monthCombo.addActionListener(e -> handleCheckBudget());
        yearCombo.addActionListener(e -> handleCheckBudget());

        // Set default to current date
        yearCombo.setSelectedItem(java.time.Year.now().getValue());
        monthCombo.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);

        setLayout(new BorderLayout());

        // TOP: Back button
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> {
            if (onBackToMenu != null) onBackToMenu.run();
        });
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        // CENTER: main content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Month + Year row: [←] Month [combo] Year [combo] [→]
        JPanel monthPanel = new JPanel();
        monthPanel.add(prevButton);
        monthPanel.add(new JLabel("Month: "));
        monthPanel.add(monthCombo);
        monthPanel.add(new JLabel("Year: "));
        monthPanel.add(yearCombo);
        monthPanel.add(nextButton);

        JPanel buttonPanel = new JPanel();
        JButton addBudgetButton = new JButton("Add Budget");
        buttonPanel.add(addBudgetButton);
        JButton resetBudgetButton = new JButton("Reset Budget");
        buttonPanel.add(resetBudgetButton);

        // Message row, centered
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(messageLabel);

        // Info panel with limit / spent / remaining / status
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));
        infoPanel.add(limitLabel);
        infoPanel.add(spentLabel);
        infoPanel.add(remainingLabel);

        // Status row: "Status: " [value]
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        JLabel statusTitleLabel = new JLabel("Status: ");
        statusPanel.add(statusTitleLabel);
        statusPanel.add(statusValueLabel);

        infoPanel.add(statusPanel);

        content.add(monthPanel);
        content.add(buttonPanel);
        content.add(messagePanel);
        content.add(Box.createVerticalStrut(10));
        content.add(infoPanel);

        // ===== Extra notes (small, non-scroll, char limit) =====
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setRows(5);
        notesArea.setColumns(20);
        notesArea.setBackground(new Color(245, 245, 245));

        notesArea.setText(NOTES_LOCKED_PLACEHOLDER);
        notesArea.setForeground(Color.GRAY);
        notesArea.setEnabled(false);

        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        notesScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notesScrollPane.setPreferredSize(new Dimension(0, 80));

        charCountLabel.setForeground(Color.GRAY);
        charCountLabel.setFont(charCountLabel.getFont().deriveFont(Font.PLAIN, 10f));

        // Placeholder behavior
        notesArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!notesArea.isEnabled()) return;

                if (notesArea.getText().equals(NOTES_PLACEHOLDER)) {
                    suppressNoteEvents = true;
                    notesArea.setText("");
                    suppressNoteEvents = false;
                    notesArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (notesArea.getText().trim().isEmpty()) {
                    notesArea.setText(NOTES_PLACEHOLDER);
                    notesArea.setForeground(Color.GRAY);
                    charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
                }
            }
        });

        // Character limit enforcement
        notesArea.getDocument().addDocumentListener(new DocumentListener() {

            private void handleUserEdit() {
                enforceNotesCharLimit();

                if (!suppressNoteEvents &&
                        notesArea.isEnabled() &&
                        !notesArea.getText().equals(NOTES_PLACEHOLDER) &&
                        !notesArea.getText().equals(NOTES_LOCKED_PLACEHOLDER)) {

                    updateLastUpdatedLabel();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) { handleUserEdit(); }

            @Override
            public void removeUpdate(DocumentEvent e) { handleUserEdit(); }

            @Override
            public void changedUpdate(DocumentEvent e) { handleUserEdit(); }
        });

        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel charCountRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        charCountRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        charCountRow.add(charCountLabel);

        notesPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        notesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        notesPanel.add(notesScrollPane);
        notesPanel.add(Box.createVerticalStrut(2));
        notesPanel.add(charCountRow);

        content.add(Box.createVerticalStrut(10));
        content.add(notesPanel);

        // ===== Last updated label =====
        Font baseFont = lastUpdatedLabel.getFont();
        lastUpdatedLabel.setFont(baseFont.deriveFont(Font.ITALIC, baseFont.getSize() - 2f));
        lastUpdatedLabel.setForeground(Color.GRAY);
        lastUpdatedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(Box.createVerticalStrut(4));
        content.add(lastUpdatedLabel);

        add(content, BorderLayout.CENTER);

        // Button handlers
        resetBudgetButton.addActionListener(e -> handleResetBudget());
        addBudgetButton.addActionListener(e -> {
            if (onAddBudgetForMonth != null) {
                Integer year = (Integer) yearCombo.getSelectedItem();
                int monthIndex = monthCombo.getSelectedIndex();
                int monthNumber = monthIndex + 1;
                String monthKey = String.format("%02d", monthNumber) + "-" + year;

                onAddBudgetForMonth.accept(monthKey);

                if (monthKey.equals(currentMonthKey) || currentMonthKey == null) {
                    currentMonthKey = monthKey;
                    updateLastUpdatedLabel();
                }
            }
        });

        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                handleCheckBudget();
            }
        });
    }

    public String formatMoney(float amount) {
        if (amount < 0) {
            return "-$" + String.format("%.2f", Math.abs(amount));
        } else {
            return "$" + String.format("%.2f", amount);
        }
    }

    private void handleCheckBudget() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        int monthIndex = monthCombo.getSelectedIndex();
        int monthNumber = monthIndex + 1;
        String monthKey = String.format("%02d", monthNumber) + "-" + year;

        Budget budget = dataAccess.getBudgetForMonth(monthKey);

        if (budget == null) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("No budget found for " + monthKey + ".");
            clearBudgetLabels();

            notesArea.setEnabled(false);
            suppressNoteEvents = true;
            notesArea.setText(NOTES_LOCKED_PLACEHOLDER);
            suppressNoteEvents = false;
            notesArea.setForeground(Color.GRAY);
            charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
            lastUpdatedLabel.setText(" ");
            currentMonthKey = null;

        } else {
            messageLabel.setForeground(new Color(4, 201, 4));
            messageLabel.setText("Budget found for " + monthKey + ".");
            limitLabel.setText("Limit: " + formatMoney(budget.getLimit()));
            spentLabel.setText("Total spent: " + formatMoney(budget.getTotalSpent()));
            remainingLabel.setText("Remaining: " + formatMoney(budget.getRemaining()));
            statusValueLabel.setText(budget.getStatus());

            if (budget.getStatus().equals("On track")) {
                statusValueLabel.setForeground(new Color(4, 201, 4));
            } else if (budget.getStatus().equals("Budget hit")) {
                statusValueLabel.setForeground(new Color(253, 218, 13));
            } else {
                statusValueLabel.setForeground(Color.RED);
            }

            currentMonthKey = monthKey;

            //----------------------------
            // LOAD SAVED TIMESTAMP HERE
            //----------------------------
            if (budget.getLastUpdated() != null && !budget.getLastUpdated().isBlank()) {
                lastUpdatedLabel.setText("Last updated: " + budget.getLastUpdated());
            } else {
                lastUpdatedLabel.setText("No updates yet");
            }

            // Load notes
            String notes = budget.getNotes();
            notesArea.setEnabled(true);

            suppressNoteEvents = true;
            if (notes == null || notes.isBlank()) {
                notesArea.setText(NOTES_PLACEHOLDER);
                notesArea.setForeground(Color.GRAY);
                charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
            } else {
                notesArea.setText(notes);
                notesArea.setForeground(Color.BLACK);
                charCountLabel.setText(notes.length() + " / " + NOTES_MAX_CHARS);
            }
            suppressNoteEvents = false;
        }
    }

    private void handleResetBudget() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        int monthIndex = monthCombo.getSelectedIndex();
        int monthNumber = monthIndex + 1;
        String monthKey = String.format("%02d", monthNumber) + "-" + year;

        Budget existing = dataAccess.getBudgetForMonth(monthKey);
        if (existing == null) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("No budget exists for " + monthKey + " to reset.");
            return;
        }

        dataAccess.deleteBudget(monthKey);

        clearBudgetLabels();
        currentMonthKey = null;

        messageLabel.setForeground(Color.RED);
        messageLabel.setText("Budget for " + monthKey + " has been reset.");
    }

    private void clearBudgetLabels() {
        limitLabel.setText("Limit: ");
        spentLabel.setText("Total spent: ");
        remainingLabel.setText("Remaining: ");
        statusValueLabel.setText("");
        statusValueLabel.setForeground(Color.BLACK);

        suppressNoteEvents = true;
        notesArea.setText(NOTES_PLACEHOLDER);
        suppressNoteEvents = false;
        notesArea.setForeground(Color.GRAY);
        notesArea.setEnabled(false);
        charCountLabel.setText("0 / " + NOTES_MAX_CHARS);

        lastUpdatedLabel.setText(" ");
    }

    private void changeMonth(int change) {
        if (currentMonthKey != null && notesArea.isEnabled()) {
            String text = notesArea.getText();
            if (!text.equals(NOTES_PLACEHOLDER)) {
                Budget current = dataAccess.getBudgetForMonth(currentMonthKey);
                if (current != null) {
                    current.setNotes(text);
                    dataAccess.saveBudget(current);
                }
            }
        }

        int monthIndex = monthCombo.getSelectedIndex();
        Integer year = (Integer) yearCombo.getSelectedItem();

        if (change == -1) {
            if (year == MIN_YEAR && monthIndex == 0) {
                prevButton.setEnabled(false);
                return;
            }
        }

        if (change == 1) {
            if (year == MAX_YEAR && monthIndex == 11) {
                nextButton.setEnabled(false);
                return;
            }
        }

        int newMonthIndex = monthIndex + change;
        int newYear = year;

        if (newMonthIndex < 0) {
            newMonthIndex = 11;
            newYear--;
        } else if (newMonthIndex > 11) {
            newMonthIndex = 0;
            newYear++;
        }

        monthCombo.setSelectedIndex(newMonthIndex);
        yearCombo.setSelectedItem(newYear);

        prevButton.setEnabled(newYear != MIN_YEAR || newMonthIndex != 0);
        nextButton.setEnabled(newYear != MAX_YEAR || newMonthIndex != 11);

        handleCheckBudget();
    }

    private void enforceNotesCharLimit() {
        if (!notesArea.isEnabled()) {
            charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
            return;
        }

        String text = notesArea.getText();
        if (text.equals(NOTES_PLACEHOLDER)) {
            charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
            charCountLabel.setForeground(Color.GRAY);
            return;
        }

        if (text.length() > NOTES_MAX_CHARS) {
            SwingUtilities.invokeLater(() -> {
                String trimmed = text.substring(0, NOTES_MAX_CHARS);
                notesArea.setText(trimmed);
                notesArea.setCaretPosition(trimmed.length());
            });
        }

        int len = Math.min(text.length(), NOTES_MAX_CHARS);
        charCountLabel.setText(len + " / " + NOTES_MAX_CHARS);

        if (len == NOTES_MAX_CHARS) {
            charCountLabel.setForeground(Color.RED);
        } else if (len > NOTES_MAX_CHARS - 15) {
            charCountLabel.setForeground(Color.RED);
        } else {
            charCountLabel.setForeground(Color.GRAY);
        }
    }

    private void updateLastUpdatedLabel() {
        ZoneId zone = ZoneId.of("America/Toronto");
        ZonedDateTime now = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        String formatted = now.format(formatter);

        lastUpdatedLabel.setText("Last updated: " + formatted);

        //-----------------------------
        // SAVE TIMESTAMP TO ENTITY
        //-----------------------------
        if (currentMonthKey != null) {
            Budget b = dataAccess.getBudgetForMonth(currentMonthKey);
            if (b != null) {
                b.setLastUpdated(formatted);
                dataAccess.saveBudget(b);
            }
        }
    }
}
