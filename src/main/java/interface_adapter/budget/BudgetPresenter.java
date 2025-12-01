package interface_adapter.budget;

import use_case.budget.BudgetOutputBoundary;
import use_case.budget.BudgetOutputData;

public class BudgetPresenter implements BudgetOutputBoundary {

    private final BudgetViewModel viewModel;

    public BudgetPresenter(BudgetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // Transfer output data from the use case into the ViewModel.
    @Override
    public void present(BudgetOutputData outputData) {
        viewModel.setMonth(outputData.month());
        viewModel.setLimit(outputData.limit());
        viewModel.setTotalSpent(outputData.totalSpent());
        viewModel.setRemaining(outputData.remaining());
        viewModel.setSuccess(outputData.success());
        viewModel.setMessage(outputData.message());
    }
}
