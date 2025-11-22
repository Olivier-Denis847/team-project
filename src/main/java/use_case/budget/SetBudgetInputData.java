package use_case.budget;

public class SetBudgetInputData {
    private final String month;
    private final float limit;
    private final float totalSpent;

    public SetBudgetInputData(String month, float limit, float totalSpent) {
        this.month = month;
        this.limit = limit;
        this.totalSpent = totalSpent;
    }

    public String getMonth() {return month;}
    public float getLimit() {return limit;}
    public float getTotalSpent() {return totalSpent;}
}
