package Olivier.interface_adapter;

import java.util.Arrays;

public class OptimizeState {
    private int time;
    private String[] labels;
    private String[] priorities;

    public void setTime (int months){this.time = months;}

    public int getTime(){return time;}

    public void setLabels (String[] labels){this.labels = labels;}

    public String[] getLabels(){return labels;}

    public void setPriorities (String[] priorities){this.priorities = priorities;}

    public String[] getPriorities(){return priorities;}

    @Override
    public String toString(){
        return "optimizeState: \n"
                + "Time: '" + time + "' months \n"
                + "Labels: '"  + Arrays.toString(labels) + "' \n"
                + "Priorities: '"  + Arrays.toString(priorities) + "' \n";
    }
}
