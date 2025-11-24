package add_transaction.interface_adapter.AddTransaction;

import add_transaction.use_case.addtransaction.AddTransactionOutputBoundary;
import add_transaction.use_case.addtransaction.AddTransactionResponseModel;

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
