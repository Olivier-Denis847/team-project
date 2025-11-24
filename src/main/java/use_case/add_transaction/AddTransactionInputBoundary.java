package use_case.add_transaction;

public interface AddTransactionInputBoundary {
    void execute(AddTransactionRequestModel requestModel);
}
