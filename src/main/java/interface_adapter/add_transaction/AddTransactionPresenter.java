package interface_adapter.add_transaction;

import use_case.add_transaction.AddTransactionOutputBoundary;
import use_case.add_transaction.AddTransactionResponseModel;

public class AddTransactionPresenter implements AddTransactionOutputBoundary {
    private final AddTransactionViewModel viewModel;

    public AddTransactionPresenter(AddTransactionViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(AddTransactionResponseModel responseModel) {
        String message = String.format(
                "Your transaction has been saved!\n" +
                        "Amount: %.1f\n" +
                        "Type: %s\n" +
                        "Note: %s\n" +
                        "Date: %s\n",
                responseModel.getAmount(),
                responseModel.getType(),
                responseModel.getNote(),
                responseModel.getDate()
        );
        viewModel.setMessage(message);
    }

    @Override
    public void prepareFailureView(String message) {
        viewModel.setMessage(message);
    }
}