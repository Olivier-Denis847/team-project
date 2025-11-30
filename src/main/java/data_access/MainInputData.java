package data_access;

public class MainInputData {
    private final String[] expenses;
    private final String[] incomes;
    private final float money;
    private final float spent;

    public MainInputData(String[] expenses, String[] incomes,
                         float money, float spent) {
        this.expenses = expenses;
        this.incomes = incomes;
        this.money = money;
        this.spent = spent;
    }

    public String[] getExpenses() {
        return expenses;
    }

    public String[] getIncomes() {
        return incomes;
    }

    public float getMoney() {
        return money;
    }

    public float getSpent() {
        return spent;
    }
}
