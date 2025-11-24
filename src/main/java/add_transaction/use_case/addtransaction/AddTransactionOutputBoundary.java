package add_transaction.use_case.addtransaction;

public interface AddTransactionOutputBoundary {
    void prepareSuccessView(AddTransactionResponseModel responseModel);
    void prepareFailureView(String errorMessage);
}
