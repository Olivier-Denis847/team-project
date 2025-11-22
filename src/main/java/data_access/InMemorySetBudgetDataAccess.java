package data_access;

import entity.Budget;
import use_case.budget.SetBudgetDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

public class InMemorySetBudgetDataAccess implements SetBudgetDataAccessInterface {

    private final Map<String, Budget> budgets = new HashMap<>();

    @Override
    public Budget getBudgetForMonth(String month) {
        return budgets.get(month);
    }

    @Override
    public void saveBudget(Budget budget) {
        budgets.put(budget.getMonth(), budget);
    }
}
