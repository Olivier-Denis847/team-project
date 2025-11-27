package interface_adapter.graph;

import use_case.graph.GraphInputBoundary;
import use_case.graph.GraphInputData;

public class GraphController {
    private final GraphInputBoundary graphUseCaseInteractor;

    public GraphController(GraphInputBoundary graphInputBoundary) {
        this.graphUseCaseInteractor = graphInputBoundary;
    }

    /*
     * Executes the graph use case
     */
    public void execute(String range, String transactionType) {
        final GraphInputData graphInputData = new GraphInputData(
                range,
                transactionType);

        this.graphUseCaseInteractor.execute(graphInputData);
    }
}
