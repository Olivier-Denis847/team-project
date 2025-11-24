package add_transaction.use_case.addtransaction;

import java.util.Date;

public class AddTransactionRequestModel {
    private final float amount;
    private final String note;
    private final String type;
    private final Date date;

    public AddTransactionRequestModel(float amount, String note, String type, Date date) {
        this.amount = amount;
        this.note = note;
        this.type = type;
        this.date = date;
    }
    public float getAmount(){
        return amount;
    }
    public String getType(){
        return type;
    }

    public String getNote(){
        return note;
    }

    public Date getDate(){
        return new Date();
    }
}
