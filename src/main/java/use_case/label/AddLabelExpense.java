package use_case.label;

import entity.Label;

import java.util.ArrayList;
import java.util.List;

public class AddLabelExpense {
    private int id;
    private boolean isExpense;
    private Double amount;
    private List<Label> labels =  new ArrayList<>();

    public AddLabelExpense(int id, Double amount, boolean isExpense) {
        this .id = id;
        this .isExpense = isExpense;
        this.amount = amount;
    }

    public void AddExpense(Label l) {
        this.labels.add(l);
    }

    public List<Label> getLabel() {
        return this.labels;
    }

    public int getId() {
        return id;
    }

}
