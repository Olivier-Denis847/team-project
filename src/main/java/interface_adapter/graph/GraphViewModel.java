package interface_adapter.graph;

public final class GraphViewModel extends ViewModel<GraphState> {
    public GraphViewModel() {
        super("Trend Graph");
        setState(new GraphState());
    }
}
