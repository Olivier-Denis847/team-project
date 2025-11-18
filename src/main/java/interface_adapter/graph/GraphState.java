package interface_adapter.graph;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * the state for the graph view model
 */
public class GraphState {
    private String selectedRange; // "Day" / "Month" / "Year"
    private String selectedType; // "Expense" / "Income"
    private Map<Integer, Float> bar; // bar data <Date, Amount>
    private Map<String, Float> pie; // category -> value

    public String getSelectedRange() {
        return selectedRange;
    }

    public void setSelectedRange(String selectedRange) {
        this.selectedRange = selectedRange;
    }

    public String getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(String selectedType) {
        this.selectedType = selectedType;
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

    public Map<Integer, Float> getBar() {
        return bar;
    }

    public void setBar(Map<Integer, Float> bar) {
        this.bar = bar;
    }

    public Map<String, Float> getPie() {
        return pie;
    }

    public void setPie(Map<String, Float> pie) {
        this.pie = pie;
    }
}
