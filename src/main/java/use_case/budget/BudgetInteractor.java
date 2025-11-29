package use_case.budget;

import entity.Budget;

public class BudgetInteractor implements BudgetInputBoundary {

    private final BudgetDataAccessInterface budgetDataAccess;
    private final BudgetOutputBoundary presenter;

    public BudgetInteractor(BudgetDataAccessInterface budgetDataAccess, BudgetOutputBoundary presenter) {
        this.budgetDataAccess = budgetDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(BudgetInputData inputData) {
        String month = inputData.getMonth();
        float limit = inputData.getLimit();
        float totalSpent = inputData.getTotalSpent();

        // Load or create entity
        Budget budget = budgetDataAccess.getBudgetForMonth(month);
        if (budget == null) {
            budget = new Budget(month);
        }

        // Update core fields
        budget.setLimit(limit);
        budget.setTotalSpent(totalSpent);

        // Update timestamp
        String timestamp = java.time.ZonedDateTime.now(java.time.ZoneId.of("America/Toronto"))
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));
        budget.setLastUpdated(timestamp);

        // Persist changes
        float remaining = budget.getRemaining();
        budgetDataAccess.saveBudget(budget);

        // Send output to presenter
        BudgetOutputData outputData =
                new BudgetOutputData(month, limit, totalSpent, remaining, true,
                        "Budget set successfully.");
        presenter.present(outputData);
    }
}