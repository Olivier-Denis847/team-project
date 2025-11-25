package entity;

public class Budget {
    private final String month;
    private float limit;
    private float totalSpent;
    private String notes;
    private String lastUpdated;

    public Budget(String month) {
        this.month = month;
        this.totalSpent = 0;
    }

    // Getters
    public String getMonth() {
        return month;
    }

    public float getLimit() {
        return limit;
    }

    public float getTotalSpent() {
        return totalSpent;
    }

    public float getRemaining() {
        return limit - totalSpent;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        if (getRemaining() < 0) {
            return "Over budget";
        } else if (getRemaining() == 0) {
            return "Budget hit";
        } else {
            return "On track";
        }
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    // Setters

    public void setLimit(float limit) {
        this.limit = limit;
    }

    public void setTotalSpent(float totalSpent) {
        this.totalSpent = totalSpent;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
