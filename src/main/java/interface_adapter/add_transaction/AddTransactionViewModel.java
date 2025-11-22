package interface_adapter.add_transaction;

public class AddTransactionViewModel {
    private String message = "";
    private Runnable onUpdate;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {

        this.message = message;

        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }
}
