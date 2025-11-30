package data_access;

import entity.Budget;
import use_case.budget.BudgetDataAccessInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Super simple text-file "database" for budgets.
 * Each line in the file:
 *   month|limit|totalSpent|lastUpdated|notes
 */
public class FileBudgetDataAccess implements BudgetDataAccessInterface {

    private final File file;
    private final Map<String, Budget> cache = new HashMap<>();

    public FileBudgetDataAccess(String filePath) {
        this.file = new File(filePath);
        loadFromFile();
    }

    @Override
    public Budget getBudgetForMonth(String month) {
        return cache.get(month);
    }

    @Override
    public void saveBudget(Budget budget) {
        cache.put(budget.getMonth(), budget);
        saveToFile();
    }

    @Override
    public void deleteBudget(String month) {
        cache.remove(month);
        saveToFile();
    }

    // Helpers

    private void loadFromFile() {
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                // month|limit|totalSpent|lastUpdated|notes
                String[] parts = line.split("\\|", -1);
                if (parts.length < 3) continue;

                String month    = parts[0];
                float limit        = Float.parseFloat(parts[1]);
                float totalSpent   = Float.parseFloat(parts[2]);
                String lastUpdated = parts.length > 3 ? parts[3] : "";
                String notes       = parts.length > 4 ? parts[4] : "";

                Budget b = new Budget(month, limit, totalSpent);
                if (hasMethodSetLastUpdated(b)) {
                    b.setLastUpdated(lastUpdated);
                }
                if (hasMethodSetNotes(b)) {
                    b.setNotes(notes);
                }

                cache.put(month, b);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read budgets file", e);
        }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (Budget b : cache.values()) {
                String line = String.join("|",
                        b.getMonth(),
                        String.valueOf(b.getLimit()),
                        String.valueOf(b.getTotalSpent()),
                        safe(b.getLastUpdated()),
                        safe(b.getNotes())
                );
                pw.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write budgets file", e);
        }
    }

    private String safe(String s) {
        return (s == null ? "" : s.replace("\n", " "));
    }

    private boolean hasMethodSetLastUpdated(Budget b) {
        try {
            b.getClass().getMethod("setLastUpdated", String.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private boolean hasMethodSetNotes(Budget b) {
        try {
            b.getClass().getMethod("setNotes", String.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}