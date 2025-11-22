package entity;

public class Budget {
    private int budgetID;
    private String month;
    private float limit;
    private float totalSpent;

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

    public String getStatus() {
        float remaining = getRemaining();
        if (remaining < 0) {
            return "Over budget";
        } else if (remaining == 0) {
            return "Exactly on budget";
        } else {
            return "On track";
        }
    }

    // You can still add: updateSpent(amount), compareToBudget(), etc. later.
}

// fix $-1000
// 1. Add budget: enter info, and cancel/save button at the bottom (save only clickable after month/budget input)
// 2. Check budget: Create a UI with dropdown menu
    // Left and right arrow for switching months
    // If budget exists display it, if not state that and feature 'add budget' button (leads to 1st menu option screen)
    // Add space for extra notes below budget (last updated date?)
    // Previous budgets table at the bottom of page with required info (can select to jump to that month)
// Each month's section should have date, budget value, total spent, remaining, success
// 3. Full year's budget: see full year's worth of budget displaying each month as well as required info in a table
    // Left and right arrow for switching years
    // Filters: [ All months | Over budget | Under budget | No budget ]
    // Click columns can sort by that value? (e.g., click remaining selects highest remaining first)
    // Select to jump to that month's budget