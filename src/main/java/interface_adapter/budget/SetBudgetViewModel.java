package interface_adapter.budget;

public class SetBudgetViewModel {
    private String month;
    private float limit;
    private float totalSpent;
    private float remaining;
    private boolean success;
    private String message;

    public String getMonth() {return month;}
    public float getLimit() {return limit;}
    public float getTotalSpent() {return totalSpent;}
    public float getRemaining() {return remaining;}
    public boolean isSuccess() {return success;}
    public String getMessage() {return message;}

    public void setMonth(String month) {this.month = month;}
    public void setLimit(float limit) {this.limit = limit;}
    public void setTotalSpent(float totalSpent) {this.totalSpent = totalSpent;}
    public void setRemaining(float remaining) {this.remaining = remaining;}
    public void setSuccess(boolean success) {this.success = success;}
    public void setMessage(String message) {this.message = message;}


}
