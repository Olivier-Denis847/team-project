package use_case.filter_transactions;

public class FilterTransactionsRequestModel {
    private final String labelInput;

    public FilterTransactionsRequestModel(String labelInput) {
        this.labelInput = labelInput;
    }

    public String getCategoryInput() {
        return labelInput;
    }
}
