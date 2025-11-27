package interface_adapter.budget;

import use_case.budget.SetBudgetInputBoundary;
import use_case.budget.SetBudgetInputData;

public class SetBudgetController {

    private final SetBudgetInputBoundary interactor;

    public SetBudgetController (SetBudgetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void setBudget(String month, float limit, float totalSpent) {
        SetBudgetInputData inputData = new SetBudgetInputData(month, limit, totalSpent);
        interactor.execute(inputData);
    }
}
