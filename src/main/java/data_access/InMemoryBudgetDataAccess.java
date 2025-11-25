package data_access;

import entity.Budget;
import use_case.budget.BudgetDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class InMemoryBudgetDataAccess implements BudgetDataAccessInterface {

    // Stores budgets using their month key as the map key
    private final Map<String, Budget> budgets = new HashMap<>();

    /**
     * Retrieve the Budget object for a given month key.
     * @param month The month key, e.g. "03-2025".
     */
    @Override
    public Budget getBudgetForMonth(String month) {
        return budgets.get(month);
    }

    /**
     * Store or update a Budget in memory.
     * The key is the month string inside the Budget entity.
     */
    @Override
    public void saveBudget(Budget budget) {
        budgets.put(budget.getMonth(), budget);
    }

    /**
     * Remove a Budget entry entirely for the given month key.
     */
    @Override
    public void deleteBudget(String monthKey) {
        budgets.remove(monthKey);
    }
}
