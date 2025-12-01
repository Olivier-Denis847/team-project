package view.budget;

import entity.Budget;
import use_case.budget.BudgetDataAccessInterface;

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

    private final transient BudgetDataAccessInterface dataAccess;

    // Month & year dropdowns
    private final JComboBox<String> monthCombo;
    private final JComboBox<Integer> yearCombo;
    private static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };

    private final JButton prevButton = new JButton("←");
    private final JButton nextButton = new JButton("→");
    private static final int MIN_YEAR = 2006;
    private static final int MAX_YEAR = 2030;

    private static final String LABEL_LIMIT = "Limit: ";
    private static final String LABEL_SPENT = "Total spent: ";
    private static final String LABEL_REMAINING = "Remaining: ";

    private final JLabel messageLabel = new JLabel(" ");
    private final JLabel limitLabel = new JLabel(LABEL_LIMIT);
    private final JLabel spentLabel = new JLabel(LABEL_SPENT);
    private final JLabel remainingLabel = new JLabel(LABEL_REMAINING);

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

        monthCombo = new JComboBox<>(MONTHS);
        yearCombo = new JComboBox<>(createYearModel());

        configureMonthYearActions();
        setDefaultMonthYearSelection();

        setLayout(new BorderLayout());
        add(buildTopPanel(onBackToMenu), BorderLayout.NORTH);
        add(buildMainContent(onAddBudgetForMonth), BorderLayout.CENTER);

        configureNotesBehavior();
        configureNavigationBehavior();
    }

    private DefaultComboBoxModel<Integer> createYearModel() {
        DefaultComboBoxModel<Integer> yearModel = new DefaultComboBoxModel<>();
        for (int y = MIN_YEAR; y <= MAX_YEAR; y++) {
            yearModel.addElement(y);
        }
        return yearModel;
    }

    private void configureMonthYearActions() {
        monthCombo.addActionListener(e -> {
            saveNote();
            handleCheckBudget();
        });
        yearCombo.addActionListener(e -> {
            saveNote();
            handleCheckBudget();
        });
    }

    private void setDefaultMonthYearSelection() {
        yearCombo.setSelectedItem(java.time.Year.now().getValue());
        monthCombo.setSelectedIndex(java.time.LocalDate.now().getMonthValue() - 1);
    }

    private JPanel buildTopPanel(Runnable onBackToMenu) {
        JButton backButton = new JButton("← BACK");
        backButton.addActionListener(e -> {
            saveNote();
            if (onBackToMenu != null) {
                onBackToMenu.run();
            }
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        return topPanel;
    }

    private JPanel buildMainContent(Consumer<String> onAddBudgetForMonth) {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel monthPanel = buildMonthPanel();
        JPanel buttonPanel = buildButtonPanel(onAddBudgetForMonth);
        JPanel messagePanel = buildMessagePanel();
        JPanel infoPanel = buildInfoPanel();
        JPanel notesPanel = buildNotesPanel();

        Font baseFont = lastUpdatedLabel.getFont();
        lastUpdatedLabel.setFont(baseFont.deriveFont(Font.ITALIC, baseFont.getSize() - 2f));
        lastUpdatedLabel.setForeground(Color.GRAY);
        lastUpdatedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(monthPanel);
        content.add(buttonPanel);
        content.add(messagePanel);
        content.add(Box.createVerticalStrut(10));
        content.add(infoPanel);
        content.add(Box.createVerticalStrut(10));
        content.add(notesPanel);
        content.add(Box.createVerticalStrut(4));
        content.add(lastUpdatedLabel);

        return content;
    }

    private JPanel buildMonthPanel() {
        JPanel monthPanel = new JPanel();
        monthPanel.add(prevButton);
        monthPanel.add(new JLabel("Month: "));
        monthPanel.add(monthCombo);
        monthPanel.add(new JLabel("Year: "));
        monthPanel.add(yearCombo);
        monthPanel.add(nextButton);
        return monthPanel;
    }

    private JPanel buildButtonPanel(Consumer<String> onAddBudgetForMonth) {
        JPanel buttonPanel = new JPanel();
        JButton addBudgetButton = new JButton("ADD / EDIT Budget");
        JButton resetBudgetButton = new JButton("RESET Budget");

        resetBudgetButton.addActionListener(e -> handleResetBudget());
        addBudgetButton.addActionListener(e -> handleAddBudget(onAddBudgetForMonth));

        buttonPanel.add(addBudgetButton);
        buttonPanel.add(resetBudgetButton);
        return buttonPanel;
    }

    private JPanel buildMessagePanel() {
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messagePanel.add(messageLabel);
        return messagePanel;
    }

    private JPanel buildInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(4, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        infoPanel.add(limitLabel);
        infoPanel.add(spentLabel);
        infoPanel.add(remainingLabel);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        JLabel statusTitleLabel = new JLabel("Status: ");
        statusPanel.add(statusTitleLabel);
        statusPanel.add(statusValueLabel);
        infoPanel.add(statusPanel);

        return infoPanel;
    }

    private JPanel buildNotesPanel() {
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

        return notesPanel;
    }

    private void configureNotesBehavior() {
        notesArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (!notesArea.isEnabled()) {
                    return;
                }
                if (notesArea.getText().equals(NOTES_PLACEHOLDER)) {
                    suppressNoteEvents = true;
                    notesArea.setText("");
                    suppressNoteEvents = false;
                    notesArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String txt = notesArea.getText().trim();
                if (txt.isEmpty()) {
                    suppressNoteEvents = true;
                    notesArea.setText(NOTES_PLACEHOLDER);
                    suppressNoteEvents = false;
                    notesArea.setForeground(Color.GRAY);
                    charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
                }
            }
        });

        notesArea.getDocument().addDocumentListener(new DocumentListener() {

            private void handleUserEdit() {
                enforceNotesCharLimit();

                if (!suppressNoteEvents && notesArea.isEnabled()
                        && !notesArea.getText().equals(NOTES_PLACEHOLDER)
                        && !notesArea.getText().equals(NOTES_LOCKED_PLACEHOLDER)) {
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
    }

    private void configureNavigationBehavior() {
        prevButton.addActionListener(e -> changeMonth(-1));
        nextButton.addActionListener(e -> changeMonth(1));

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                handleCheckBudget();
            }
        });
    }

    private void handleAddBudget(Consumer<String> onAddBudgetForMonth) {
        saveNote(); // Save current note

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
    }

    // Logic for check budget screen
    private void handleCheckBudget() {
        Integer year = (Integer) yearCombo.getSelectedItem();
        int monthIndex = monthCombo.getSelectedIndex();
        int monthNumber = monthIndex + 1;
        String monthKey = String.format("%02d", monthNumber) + "-" + year;

        Budget budget = dataAccess.getBudgetForMonth(monthKey);

        if (budget == null) {
            messageLabel.setForeground(Color.RED);
            messageLabel.setText("No budget found for " + monthKey + ".");
            clearLabels();

            notesArea.setEnabled(false);
            suppressNoteEvents = true;
            notesArea.setText(NOTES_LOCKED_PLACEHOLDER);
            suppressNoteEvents = false;
            notesArea.setForeground(Color.GRAY);
            charCountLabel.setText("0 / " + NOTES_MAX_CHARS);
            lastUpdatedLabel.setText(" ");
            currentMonthKey = null;

        }
        else {
            messageLabel.setForeground(new Color(4, 201, 4));
            messageLabel.setText("Budget found for " + monthKey + ".");
            limitLabel.setText(LABEL_LIMIT + formatMoney(budget.getLimit()));
            spentLabel.setText(LABEL_SPENT + formatMoney(budget.getTotalSpent()));
            remainingLabel.setText(LABEL_REMAINING + formatMoney(budget.getRemaining()));
            statusValueLabel.setText(budget.getStatus());

            if (budget.getStatus().equals("On track")) {
                statusValueLabel.setForeground(new Color(4, 201, 4));
            }
            else if (budget.getStatus().equals("Budget hit")) {
                statusValueLabel.setForeground(new Color(253, 218, 13));
            }
            else {
                statusValueLabel.setForeground(Color.RED);
            }

            currentMonthKey = monthKey;

            // Load timestamp
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

    // Logic for resetting budget
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

        clearLabels();
        currentMonthKey = null;

        messageLabel.setForeground(Color.RED);
        messageLabel.setText("Budget for " + monthKey + " has been reset.");
    }

    // Return screen to template without data
    private void clearLabels() {
        limitLabel.setText(LABEL_LIMIT);
        spentLabel.setText(LABEL_SPENT);
        remainingLabel.setText(LABEL_REMAINING);
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

    // Logic for changing through months
    private void changeMonth(int change) {
        saveNote();

        int monthIndex = monthCombo.getSelectedIndex();
        Integer year = (Integer) yearCombo.getSelectedItem();

        if (change == -1 && year == MIN_YEAR && monthIndex == 0) {
                prevButton.setEnabled(false);
                return;
            }


        if (change == 1 && year == MAX_YEAR && monthIndex == 11) {
            nextButton.setEnabled(false);
            return;
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

    // Ensure user can't type more than max character count
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
        }
        else if (len > NOTES_MAX_CHARS - 15) {
            charCountLabel.setForeground(Color.RED);
        }
        else {
            charCountLabel.setForeground(Color.GRAY);
        }
    }

    // Update timestamp
    private void updateLastUpdatedLabel() {
        ZoneId zone = ZoneId.of("America/Toronto");
        ZonedDateTime now = ZonedDateTime.now(zone);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");
        String formatted = now.format(formatter);

        lastUpdatedLabel.setText("Last updated: " + formatted);

        // Save timestamp to entity
        if (currentMonthKey != null) {
            Budget b = dataAccess.getBudgetForMonth(currentMonthKey);
            if (b != null) {
                b.setLastUpdated(formatted);
                dataAccess.saveBudget(b);
            }
        }
    }

    // Logic for saving current note
    private void saveNote() {
        if (currentMonthKey == null || !notesArea.isEnabled()) return;

        Budget b = dataAccess.getBudgetForMonth(currentMonthKey);
        if (b == null) return;

        String text = notesArea.getText();

        // empty or placeholder -> clear notes
        if (text.trim().isEmpty() || text.equals(NOTES_PLACEHOLDER)) {
            b.setNotes("");
        } else {
            b.setNotes(text);
        }

        dataAccess.saveBudget(b);
    }

    // Used by other views to jump directly to a specific month/year
    public void setMonthYearFromKey(String key) {
        String[] p = key.split("-");
        monthCombo.setSelectedIndex(Integer.parseInt(p[0]) - 1);
        yearCombo.setSelectedItem(Integer.parseInt(p[1]));
        handleCheckBudget();
    }

    // Ensure dollar sign and negative values are properly formatted
    public String formatMoney(float amount) {
        if (amount < 0) {
            return "-$" + String.format("%.2f", Math.abs(amount));
        } else {
            return "$" + String.format("%.2f", amount);
        }
    }
}