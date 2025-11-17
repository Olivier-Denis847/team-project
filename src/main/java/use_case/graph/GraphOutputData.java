package use_case.graph;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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

    public List<Integer> getX() {
        if (bar == null)
            return null;
        return new ArrayList<>(bar.keySet());
    }

    public List<Float> getY() {
        if (bar == null)
            return null;
        return new ArrayList<>(bar.values());
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
