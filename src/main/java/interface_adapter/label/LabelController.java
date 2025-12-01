package interface_adapter.label;

import entity.Label;
import use_case.label.LabelUserCase;

import java.util.List;

public class LabelController {

    private LabelUserCase userCase;

    public LabelController(LabelUserCase userCase) {
        this.userCase = userCase;
    }

    public String createLabel(String name, double amount, String description, String color, int userId) {
        Label label = new Label(0, name, color, description);
        return userCase.createLabel(label);
    }

    public String editLabel(Label label) {
        return userCase.editLabel(label);
    }

    public void assignLabelToExpense(int transactionId, Label label) {
        userCase.assignLabelToExpense(transactionId, label);
    }

    public void removeLabelFromExpense(int transactionId, int labelId) {
        userCase.removeLabelFromExpense(transactionId, labelId);
    }

    public String deleteLabel(int labelId) {
        return userCase.deleteLabel(labelId);
    }

    public List<Label> getAllLabels(int userId) {
        return userCase.getAllLabels(userId);
    }
}
