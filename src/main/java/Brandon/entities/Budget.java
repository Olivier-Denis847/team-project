package Brandon.entities;

public class Budget {
    private int budgetID;
    private String month;
    private float limit;
    private float totalSpent;
    private String notes;
    private String lastUpdated;

    public Budget(String month) {
        this.month = month;
        this.totalSpent = 0;
    }

    public int getBudgetID() {
        return budgetID;
    }

    public void setBudgetID(int budgetID) {
        this.budgetID = budgetID;
    }

    public String getMonth() {
        return month;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }

    public float getLimit() {
        return limit;
    }

    public void setTotalSpent(float totalSpent) {
        this.totalSpent = totalSpent;
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

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        float remaining = getRemaining();
        if (remaining < 0) {
            return "Over budget";
        } else if (remaining == 0) {
            return "Budget hit";
        } else {
            return "On track";
        }
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

// 3. Full year's budget: see full year's worth of budget displaying each month as well as required info in a table
    // Left and right arrow for switching years
    // Filters: [ All months | Over budget | Under budget | No budget ]
    // Click columns can sort by that value? (e.g., click remaining selects highest remaining first)
    // Select to jump to that month's budget
// Extra code contribution other than use case (TODO in MainView file)
// Review another pull request
// Update presentation (UML, screenshots of use case)
// UML