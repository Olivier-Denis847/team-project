package use_case.label;

import entity.Label;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class AddLabelExpense {
    private int id;
    private boolean isExpense;
    private Double amount;
    private String[] colorOptions = { "Red", "Blue", "Green", "Display Graph", "Display on Home Page" };
    private JComboBox<String> colorBox = new JComboBox<>(colorOptions);
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
