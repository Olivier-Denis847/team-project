package data_access;

import entity.Label;
import entity.Transaction;
import use_case.graph.GraphDataAccessInterface;

import java.util.*;

public class GraphTestDataAccess implements GraphDataAccessInterface {
    private final List<Transaction> allEntries;
    private String range = "Month";
    private String type = "Expense";
    private final Random random = new Random();

    public GraphTestDataAccess() {
        allEntries = new ArrayList<>();

        // Define Categories
        // Note: The 'new Date()' here is just for the Label creation, not the transaction itself.
        Label housing = new Label(1, "Housing", "blue", 1, 2500.0, "Mortgage/Rent");
        Label groceries = new Label(2, "Groceries", "green", 1, 200.0, "Supermarket");
        Label utilities = new Label(3, "Utilities", "orange", 1, 150.0, "Bills");
        Label transport = new Label(4, "Transport", "gray", 1, 60.0, "Gas/Car");
        Label healthcare = new Label(5, "Healthcare", "red", 1, 100.0, "Medical");
        Label education = new Label(6, "Education", "purple", 1, 500.0, "School/Kids");
        Label dining = new Label(7, "Dining Out", "pink", 1, 80.0, "Restaurants");
        Label shopping = new Label(8, "Shopping", "cyan", 1, 100.0, "Clothing/Misc");
        Label incomePrimary = new Label(9, "Salary (Pri)", "darkgreen", 1, 3500.0, "Main Job");
        Label incomeSec = new Label(10, "Salary (Sec)", "lightgreen", 1, 1200.0, "Part-time/Gig");

        Calendar cal = Calendar.getInstance();
        // Start Date: January 1, 2022
        cal.set(2022, Calendar.JANUARY, 1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date today = new Date();
        int id = 1;

        // LOOP: Advances one day at a time
        while (cal.getTime().before(today)) {

            // CRITICAL: Generate a FRESH Date object for this specific iteration
            // This ensures every loop has a unique date object in memory.
            Date currentDate = cal.getTime();

            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int month = cal.get(Calendar.MONTH);

            // --- FIXED RECURRING TRANSACTIONS ---

            // 1st of Month: Mortgage
            if (dayOfMonth == 1) {
                allEntries.add(new Transaction(id++, 2400.00f, Arrays.asList(housing),
                        "Monthly Mortgage", currentDate, "Expense"));
            }

            // 15th of Month: Utilities
            if (dayOfMonth == 15) {
                float utilCost = 150.0f + random.nextFloat() * 100.0f;
                if (month == Calendar.JULY || month == Calendar.AUGUST || month == Calendar.JANUARY) {
                    utilCost += 80.0f;
                }
                allEntries.add(new Transaction(id++, utilCost, Arrays.asList(utilities),
                        "Utilities", currentDate, "Expense"));
            }

            // 5th of Month: Education
            if (dayOfMonth == 5) {
                allEntries.add(new Transaction(id++, 650.00f, Arrays.asList(education),
                        "Tuition", currentDate, "Expense"));
            }

            // --- INCOME ---

            // Bi-weekly Salary (1st and 15th)
            if (dayOfMonth == 1 || dayOfMonth == 15) {
                allEntries.add(new Transaction(id++, 3200.00f, Arrays.asList(incomePrimary),
                        "Salary", currentDate, "Income"));
            }

            // Random Secondary Income
            if (random.nextInt(100) < 15) {
                float amount = 100.0f + random.nextFloat() * 400.0f;
                allEntries.add(new Transaction(id++, round(amount), Arrays.asList(incomeSec),
                        "Freelance", currentDate, "Income"));
            }

            // --- DAILY EXPENSES ---

            int dailyTransactions = random.nextInt(3);
            if ((dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) && dailyTransactions == 0) {
                dailyTransactions = 1;
            }

            for (int i = 0; i < dailyTransactions; i++) {
                int typeChance = random.nextInt(100);

                if (typeChance < 40) {
                    float amount = 25.0f + random.nextFloat() * 150.0f;
                    if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) amount += 80.0f;
                    allEntries.add(new Transaction(id++, round(amount), Arrays.asList(groceries),
                            "Groceries", currentDate, "Expense"));
                } else if (typeChance < 60) {
                    float amount = 40.0f + random.nextFloat() * 40.0f;
                    allEntries.add(new Transaction(id++, round(amount), Arrays.asList(transport),
                            "Transport", currentDate, "Expense"));
                } else if (typeChance < 80) {
                    float amount = 15.0f + random.nextFloat() * 80.0f;
                    if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) amount += 50.0f;
                    allEntries.add(new Transaction(id++, round(amount), Arrays.asList(dining),
                            "Dining", currentDate, "Expense"));
                } else if (typeChance < 95) {
                    float amount = 20.0f + random.nextFloat() * 150.0f;
                    allEntries.add(new Transaction(id++, round(amount), Arrays.asList(shopping),
                            "Shopping", currentDate, "Expense"));
                } else {
                    float amount = 20.0f + random.nextFloat() * 80.0f;
                    allEntries.add(new Transaction(id++, round(amount), Arrays.asList(healthcare),
                            "Healthcare", currentDate, "Expense"));
                }
            }

            // ADVANCE DATE
            // This modifies the Calendar state for the NEXT iteration
            cal.add(Calendar.DATE, 1);
        }

        System.out.println("Generated " + allEntries.size() + " transactions.");
    }

    private float round(float value) {
        return (float) (Math.round(value * 100.0) / 100.0);
    }

    @Override
    public List<Transaction> getAllEntries() { return this.allEntries; }
    @Override
    public void saveGraphRange(String lineGraphRange) { this.range = lineGraphRange; }
    @Override
    public void saveGraphType(String type) { this.type = type; }
    @Override
    public String getRange() { return this.range; }
    @Override
    public String getType() { return this.type; }

    @Override
    public List<Transaction> getExpenses() {
        List<Transaction> expenses = new ArrayList<>();
        for (Transaction t : this.allEntries) {
            if ("Expense".equalsIgnoreCase(t.getType())) expenses.add(t);
        }
        return expenses;
    }

    @Override
    public List<Transaction> getIncomes() {
        List<Transaction> incomes = new ArrayList<>();
        for (Transaction t : this.allEntries) {
            if ("Income".equalsIgnoreCase(t.getType())) incomes.add(t);
        }
        return incomes;
    }
}