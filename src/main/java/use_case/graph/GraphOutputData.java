package use_case.graph;

import java.util.List;
import java.util.Map;

public class GraphOutputData {
    private String selectedRange;
    private String selectedType;
    private Map<Integer, Float> bar;
    private Map<String, Float> pie;
    private List<String> alerts;

    public GraphOutputData(String selectedRange,
            String selectedType,
            Map<Integer, Float> bar,
            Map<String, Float> pie,
            List<String> alerts) {
        this.selectedRange = selectedRange;
        this.selectedType = selectedType;
        this.bar = bar;
        this.pie = pie;
        this.alerts = alerts;
    }

    public String getSelectedRange() {
        return selectedRange;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public Map<String, Float> getPie() {
        return pie;
    }

    public Map<Integer, Float> getBar() {
        return bar;
    }

    public List<String> getAlerts() {
        return alerts;
    }
}
