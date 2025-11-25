package interface_adapter.budget;

import use_case.budget.BudgetInputBoundary;
import use_case.budget.BudgetInputData;

public class BudgetController {

    private final BudgetInputBoundary interactor;

    public BudgetController(BudgetInputBoundary interactor) {
        this.interactor = interactor;
    }

    // Pass user input to interactor as a data object
    public void setBudget(String month, float limit, float totalSpent) {
        BudgetInputData inputData = new BudgetInputData(month, limit, totalSpent);
        interactor.execute(inputData);
    }
}
