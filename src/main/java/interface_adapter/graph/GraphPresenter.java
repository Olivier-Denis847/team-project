package interface_adapter.graph;

import use_case.graph.GraphOutputBoundary;
import use_case.graph.GraphOutputData;

public class GraphPresenter implements GraphOutputBoundary {
    private final GraphViewModel gvm;

    public GraphPresenter(GraphViewModel gvm) {
        this.gvm = gvm;
    }

    @Override
    public void prepareGraph(GraphOutputData data) {
        final GraphState graphState = gvm.getState();

        // update change for every attribute in data unless its null
        if (data.getSelectedRange() != null)
            graphState.setSelectedRange(data.getSelectedRange());
        if (data.getSelectedType() != null)
            graphState.setSelectedType(data.getSelectedType());
        if (data.getBar() != null)
            graphState.setBar(data.getBar());
        if (data.getPie() != null)
            graphState.setPie(data.getPie());
        if (data.getLabelColors() != null)
            graphState.setLabelColors(data.getLabelColors());
        gvm.firePropertyChange();

        // TODO: implement ALERT logic
    }

    @Override
    public void prepareAlert(GraphOutputData data) {

    }
}