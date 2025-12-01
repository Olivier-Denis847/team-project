package app;

import data_access.FinanceDataAccess;
import interface_adapter.graph.GraphController;
import interface_adapter.graph.GraphPresenter;
import interface_adapter.graph.GraphViewModel;
import use_case.graph.GraphDataAccessInterface;
import use_case.graph.GraphInteractor;
import view.graph.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GraphApp {

    private GraphApp(){
        throw new IllegalStateException("Utility class");
    }
    public static void start(FinanceDataAccess financeDataAccess) {
        // Set up presenter and view model
        GraphViewModel viewModel = new GraphViewModel();
        GraphPresenter presenter = new GraphPresenter(viewModel);

        // Set up interactor
        GraphInteractor interactor = new GraphInteractor(financeDataAccess, presenter);

        // Set up controller
        GraphController controller = new GraphController(interactor);

        // Set up view
        GraphPanel panel = new GraphPanel(viewModel);
        panel.setGraphController(controller);

        viewModel.addPropertyChangeListener(panel);

        // Execute default view
        controller.execute("Day", "Expense");

        JFrame frame = new JFrame("Trend Graph");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setLocationRelativeTo(null); // center the new frame
    }
}
