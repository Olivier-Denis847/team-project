package optimize.use_case;

public class OptimizeInputData {

    private final int months;
    private final String[] labels;
    private final String[] priorities;

    public OptimizeInputData(int months, String[] labels, String[] priorities) {
        this.months = months;
        this.labels = labels;
        this.priorities = priorities;
    }

    public int getMonths() {return months;}

    public String[] getLabels() {return labels;}

    public String[] getPriorities() {return priorities;}
}
