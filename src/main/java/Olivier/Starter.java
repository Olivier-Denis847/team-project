package Olivier;

import Olivier.interface_adapter.*;
import Olivier.use_case.*;
import javax.swing.*;

public class Starter {
    public static void main(String[] args) {
        OptimizeViewModel viewModel = new OptimizeViewModel();
        OptimizePresenter presenter = new OptimizePresenter(viewModel);
        OptimizeDataAccess dataAccess = new OptimizeDataAccess();
        OptimizeInteractor interactor = new OptimizeInteractor(dataAccess, presenter);
        OptimizeController controller = new OptimizeController(interactor);
        OptimizeView optimizeView = new OptimizeView(viewModel);
        optimizeView.setController(controller);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(optimizeView);
        frame.pack();
        frame.setVisible(true);

    }
}
