package entity;

/*
* It is representing a label/category that can be added to transactions.
**/
public class Label {
    private int labelId;
    private String labelName;
    private String description;
    private String color; // color will be stored as hex

    public Label(int labelId, String labelName, String color, String description) {
        this.labelId = labelId;
        this.labelName = labelName;
        this.color = color;
        this.description = description;
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
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return labelName;
    }
}
