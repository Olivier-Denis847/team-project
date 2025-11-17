package Entity;

import java.util.Date;
import java.util.List;

/*
 * Entity class representing a financial transaction.
 */
public class Transaction {
    private long id;
    private float amount;
    private List<Label> labels;
    private String note;
    private Date date;
    private String type;

    /*
     * Constructor for Transaction class.
     * this constructor takes in parameters for faster Transaction creation
     */
    public Transaction(long id, float amount, List<Label> labels, String note, Date date, String type) {
        this.id = id;
        this.amount = amount;
        this.labels = labels;
        this.note = note;
        this.date = date;
        this.type = type;
    }

    // Getters and Setters for each field
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}