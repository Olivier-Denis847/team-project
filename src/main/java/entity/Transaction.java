package entity;

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
    private String type; // either "income" or "expense", not case-sensitive
    private Category category;

    /*
     * Constructor for Transaction class.
     * this constructor takes in parameters for faster Transaction creation
     */
    public Transaction(long id, float amount, List<Label> labels, String note, Date date, String type, Category category) {
        this.id = id;
        this.amount = amount;
        this.labels = labels;
        this.note = note;
        this.date = date;
        this.type = type;
        this.category = category;
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

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", note='" + note + '\'' +
                ", labels='" + labels + '\'' +
                ", date=" + date +
                ", category=" + category +
                '}';
    }

}