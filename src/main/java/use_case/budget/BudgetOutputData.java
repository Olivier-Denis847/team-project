package use_case.budget;

public record BudgetOutputData(String month, float limit, float totalSpent, float remaining, boolean success,
                               String message) {
}
