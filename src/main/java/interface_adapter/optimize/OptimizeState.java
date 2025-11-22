package interface_adapter.optimize;

import java.util.Arrays;

public class OptimizeState {
    private int time;
    private String[] labels;
    private String[] priorities;
    private String resultMessage;
    private String errorMessage;

    public OptimizeState(OptimizeState other) {
        if (other == null) return;

        this.time = other.time;

        // Defensive copy for arrays
        this.labels = (other.labels != null)
                ? other.labels.clone()
                : null;

        this.priorities = (other.priorities != null)
                ? other.priorities.clone()
                : null;

        this.resultMessage = other.resultMessage;
        this.errorMessage = other.errorMessage;
    }

    public void setTime (int months){this.time = months;}
    public int getTime(){return time;}

    public void setLabels (String[] labels){this.labels = labels;}
    public String[] getLabels(){return labels;}

    public void setPriorities (String[] priorities){this.priorities = priorities;}
    public String[] getPriorities(){return priorities;}

    public void setResult(String msg) { this.resultMessage = msg; }
    public String getResult() { return resultMessage; }

    public void setError(String err) { this.errorMessage = err; }
    public String getError() { return errorMessage; }

    @Override
    public String toString(){
        return "optimizeState: \n"
                + "Time: '" + time + "' months \n"
                + "Labels: '"  + Arrays.toString(labels) + "' \n"
                + "Priorities: '"  + Arrays.toString(priorities) + "' \n"
                + "Result: '" + resultMessage + "'\n"
                + "Error: '" + errorMessage + "'";
    }
}
