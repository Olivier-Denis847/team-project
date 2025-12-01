package interface_adapter.budget;

public class BudgetViewModel {
    private String month;
    private boolean success;
    private String message;

    // Getters for the UI
    public String getMonth() {return month;}
    public boolean isSuccess() {return success;}
    public String getMessage() {return message;}

    // Setters used by the presenter
    public void setMonth(String month) {this.month = month;}
    public void setLimit(float limit) {
        // required by presenter
    }
    public void setTotalSpent(float totalSpent) {
        // required by presenter
    }
    public void setRemaining(float remaining) {
        // required by presenter
    }
    public void setSuccess(boolean success) {this.success = success;}
    public void setMessage(String message) {this.message = message;}
}