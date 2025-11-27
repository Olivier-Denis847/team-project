package use_case.add_transaction;

public interface AddTransactionOutputBoundary {
    void prepareSuccessView(AddTransactionResponseModel responseModel);
    void prepareFailureView(String errorMessage);
}