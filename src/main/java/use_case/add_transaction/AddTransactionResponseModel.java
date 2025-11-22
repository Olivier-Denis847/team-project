package use_case.add_transaction;

public class AddTransactionResponseModel {
    private final long transactionId;
    private final float amount;
    private final String type;
    private final String note;
    private final String date;

    public AddTransactionResponseModel(long transactionId, float amount, String type, String note, String date) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.note = note;
        this.date = date;
    }

    public long getTransactionId() {
        return  transactionId;
    }

    public float getAmount() {
        return  amount;
    }

    public String getType() {
        return  type;
    }

    public String getNote() {
        return  note;
    }

    public String getDate() {
        return  date;
    }
}
