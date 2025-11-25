package app;

import data_access.OptimizeDataAccess;
import interface_adapter.optimize.OptimizeController;
import interface_adapter.optimize.OptimizePresenter;
import interface_adapter.optimize.OptimizeViewModel;
import use_case.optimize.OptimizeInteractor;
import view.optimize.OptimizeView;

import javax.swing.*;

public class OptimizeApp {
    public static void main(String[] args) {
        OptimizeViewModel viewModel = new OptimizeViewModel();
        OptimizePresenter presenter = new OptimizePresenter(viewModel);
        OptimizeDataAccess dataAccess = new OptimizeDataAccess();
        OptimizeInteractor interactor = new OptimizeInteractor(dataAccess, presenter);
        OptimizeController controller = new OptimizeController(interactor);
        OptimizeView optimizeView = new OptimizeView(viewModel);
        optimizeView.setController(controller);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(optimizeView);
        frame.pack();
        frame.setVisible(true);

    }
}
