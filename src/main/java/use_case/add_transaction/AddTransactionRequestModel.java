package use_case.add_transaction;

import java.util.Date;

public class AddTransactionRequestModel {
    private final float amount;
    private final String note;
    private final String type;
    private final Date date;
    private final String categoryInput;

    public AddTransactionRequestModel(float amount, String note, String type, Date date, String categoryInput) {
        this.amount = amount;
        this.note = note;
        this.type = type;
        this.date = date;
        this.categoryInput = categoryInput;
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

    public String getCategoryInput() { return categoryInput; }
}