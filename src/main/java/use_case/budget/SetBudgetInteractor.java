package use_case.budget;

import entity.Budget;

public class SetBudgetInteractor implements SetBudgetInputBoundary {

    private final SetBudgetDataAccessInterface budgetDataAccess;
    private final SetBudgetOutputBoundary presenter;

    public SetBudgetInteractor(SetBudgetDataAccessInterface budgetDataAccess,
                               SetBudgetOutputBoundary presenter) {
        this.budgetDataAccess = budgetDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(SetBudgetInputData inputData) {
        String month = inputData.getMonth();
        float limit = inputData.getLimit();
        float totalSpent = inputData.getTotalSpent();

        // Validate
        if (limit <= 0) {
            SetBudgetOutputData outputData =
                    new SetBudgetOutputData(
                            month, limit, totalSpent, limit - totalSpent, false,
                            "Budget must be positive."
                    );
            presenter.present(outputData);
            return;
        }

        if (totalSpent <= 0) {
            SetBudgetOutputData outputData =
                    new SetBudgetOutputData
                            (month, limit, totalSpent, limit - totalSpent, false,
                                    "Spent amount cannot be negative."
                            );
            presenter.present(outputData);
            return;
        }

        if (month == null || month.isBlank()) {
            SetBudgetOutputData outputData =
                    new SetBudgetOutputData(
                            month, limit, totalSpent, limit - totalSpent, false,
                            "Month cannot be empty."
                    );
            presenter.present(outputData);
            return;
        }

        // Load or create Budget entity
        Budget budget = budgetDataAccess.getBudgetForMonth(month);
        if (budget == null) {
            budget = new Budget(month);
        }
        budget.setLimit(limit);
        budget.setTotalSpent(totalSpent);
        float remaining = budget.getRemaining();

        // Save through DAO
        budgetDataAccess.saveBudget(budget);

        // Build output and notify presenter
        SetBudgetOutputData outputData =
                new SetBudgetOutputData(month, limit, totalSpent, remaining, false,
                        "Budget set successfully.");
        presenter.present(outputData);
    }
}
