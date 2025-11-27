package interface_adapter.budget;

import use_case.budget.SetBudgetOutputBoundary;
import use_case.budget.SetBudgetOutputData;

public class SetBudgetPresenter implements SetBudgetOutputBoundary {

    private final SetBudgetViewModel viewModel;

    public SetBudgetPresenter(SetBudgetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(SetBudgetOutputData outputData) {
        viewModel.setMonth(outputData.getMonth());
        viewModel.setLimit(outputData.getLimit());
        viewModel.setTotalSpent(outputData.getTotalSpent());
        viewModel.setRemaining(outputData.getRemaining());
        viewModel.setSuccess(outputData.getSuccess());
        viewModel.setMessage(outputData.getMessage());
    }
}
