package entity;


/*
* It is representing user, which can add the label for expense according to the object name, date,
* and amount.
**/

public class Label {
    private int labelId;
    private String labelName;
    private double amount;
    private String description;
    private String colorHex;
    private int userid;
    public Label(int labelId, String labelName, String colorHex,
                 int userid, double amount, String description) {
        this.labelId = labelId;
        this.labelName = labelName;
        this.colorHex = colorHex;
        this.description = description;
        this.userid = userid;
        this.amount = amount;

    }

    public int getLabelId() {
        return labelId;
    }

    public void setLabelId(int labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
    public String getColor() {
        return colorHex;
    }

    public void setColor(String colorHex) {
        this.colorHex = colorHex;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId(){
        return this.userid;
    }

    @Override
    public String toString() {
        return labelName;
    }
}
