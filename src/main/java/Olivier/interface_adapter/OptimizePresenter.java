package Olivier.interface_adapter;

import Olivier.use_case.OptimizeInputBoundary;
import Olivier.use_case.OptimizeOutputBoundary;
import Olivier.use_case.OptimizeOutputData;

public class OptimizePresenter implements OptimizeOutputBoundary {
    private final OptimizeViewModel viewModel;

    public OptimizePresenter (OptimizeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void successView (OptimizeOutputData data) {
        //Temporary solution
        System.out.println(data.getMessage());
    }

    @Override
    public void failureView (String errorMessage) {
        //Temporary solution
        System.out.println(errorMessage);
    }

    @Override
    public void cancelView () {
        //Implement after making a home page
    }

}
