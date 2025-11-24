package interface_adapter.Label;

import entity.Label;
import use_case.LabelUserCase;

import java.util.List;

public class LabelController {

    private LabelUserCase userCase;

    public LabelController(LabelUserCase userCase) {
        this.userCase = userCase;
    }

    // CREATE LABEL
    public String createLabel(String name, double amount, String description, String color, int userId) {

        Label label = new Label(
                0,          // id 0 → auto-incremented by DAO
                name,
                color,       // no date needed anymore (design change)
                userId,
                amount,
                description
        );

        return userCase.createLabel(label);
    }

    // EDIT LABEL
    public String editLabel(Label label) {
        return userCase.editLabel(label);
    }

    // ASSIGN LABEL TO EXPENSE
    public String assignLabelToAle(int expenseId, int labelId) {
        return userCase.assignLabelToAle(expenseId, labelId);
    }

    // DELETE LABEL
    public String deleteLabel(int labelId) {
        return userCase.deleteLabel(labelId);
    }

    // GET LABELS
    public List<Label> getAllLabels(int userId) {
        return userCase.getAllLabels(userId);
    }
}


