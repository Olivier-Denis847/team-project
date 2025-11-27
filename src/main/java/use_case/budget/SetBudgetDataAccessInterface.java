package use_case.budget;

import entity.Budget;

public interface SetBudgetDataAccessInterface {
    Budget getBudgetForMonth(String month);
    void saveBudget(Budget budget);
}
