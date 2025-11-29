package use_case.budget;

import entity.Budget;

public interface BudgetDataAccessInterface {
    Budget getBudgetForMonth(String month);
    void saveBudget(Budget budget);
    void deleteBudget(String monthKey);
}
