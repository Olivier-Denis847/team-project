package app;

import data_access.GraphTestDataAccess;
import interface_adapter.graph.GraphController;
import interface_adapter.graph.GraphPresenter;
import interface_adapter.graph.GraphViewModel;
import use_case.graph.GraphDataAccessInterface;
import use_case.graph.GraphInteractor;
import view.graph.GraphPanel;

import javax.swing.*;
import java.awt.*;

public class GraphApp {
    public static void showGraphView() {
        // Set up test data access
        /* TODO: currently using the graphTestDataAccess, switch this
         * to actual data access
         */
        GraphDataAccessInterface dataAccess = new GraphTestDataAccess();

        // Set up presenter and view model
        GraphViewModel viewModel = new GraphViewModel();
        GraphPresenter presenter = new GraphPresenter(viewModel);

        // Set up interactor
        GraphInteractor interactor = new GraphInteractor(dataAccess, presenter);

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
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setBackground(Color.WHITE);
        frame.setLocationRelativeTo(null); // center the new frame
    }
}
