package use_case.graph;

public interface GraphOutputBoundary {

    /**
     * prepares the updateded graph view with output data
     * 
     * @param graphOutputData the output data
     */
    void prepareGraph(GraphOutputData graphOutputData);

    /**
     * prepares the alert in case of failure
     * 
     * @param graphOutputData the output data
     */
    void prepareAlert(GraphOutputData graphOutputData);
}
