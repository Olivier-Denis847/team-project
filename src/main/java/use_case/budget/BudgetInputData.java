package use_case.budget;

public class BudgetInputData {
    private final String month;
    private final float limit;
    private final float totalSpent;

    public BudgetInputData(String month, float limit, float totalSpent) {
        this.month = month;
        this.limit = limit;
        this.totalSpent = totalSpent;
    }

    // Getters
    public String getMonth() {return month;}
    public float getLimit() {return limit;}
    public float getTotalSpent() {return totalSpent;}
}
