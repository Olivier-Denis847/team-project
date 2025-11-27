package use_case.budget;

public class SetBudgetOutputData {
    private final String month;
    private final float limit;
    private final float totalSpent;
    private final float remaining;
    private final boolean success;
    private final String message;

    public SetBudgetOutputData(String month, float limit, float totalSpent, float remaining, boolean success, String message) {
        this.month = month;
        this.limit = limit;
        this.totalSpent = totalSpent;
        this.remaining = remaining;
        this.success = success;
        this.message = message;
    }

    public String getMonth() {return month;}
    public float getLimit() {return limit;}
    public float getTotalSpent() {return totalSpent;}
    public float getRemaining() {return remaining;}
    public boolean getSuccess() {return success;}
    public String getMessage() {return message;}
}
