package Olivier.interface_adapter;

import Olivier.use_case.OptimizeOutputBoundary;
import Olivier.use_case.OptimizeOutputData;

public class OptimizePresenter implements OptimizeOutputBoundary {
    private final OptimizeViewModel viewModel;

    public OptimizePresenter (OptimizeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void successView (OptimizeOutputData data) {
        OptimizeState state = viewModel.getState();

        state.setResult(data.getMessage());
        state.setError(null);

        viewModel.setState(state);
    }

    @Override
    public void failureView (String errorMessage) {
        OptimizeState state = viewModel.getState();

        state.setResult(null);
        state.setError(errorMessage);

        viewModel.setState(state);
    }

    @Override
    public void cancelView () {
        //Implement after making a home page
    }

}
