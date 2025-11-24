package Qi;

import java.util.List;

public class LabelUserCaseImp implements LabelUserCase {

    private LabelDataAccess labelDataAccess;
    private ALEDataAccess aleDataAccess;

    public LabelUserCaseImp(LabelDataAccess labelDataAccess, ALEDataAccess aleDataAccess) {
        this.labelDataAccess = labelDataAccess;
        this.aleDataAccess = aleDataAccess;
    }

    @Override
    public String createLabel(Label label) {

        // Validation
        if (label.getLabelName() == null || label.getLabelName().trim().isEmpty()) {
            return "Label name cannot be empty.";
        }

        if (label.getAmount() < 0) {
            return "Amount cannot be negative.";
        }

        if (label.getColor() == null || label.getColor().trim().isEmpty()) {
            return "Color cannot be empty.";
        }

        // Prevent duplicates by name
        if (labelDataAccess.labelExists(label.getUserId(), label.getLabelName())) {
            return "Label with this name already exists.";
        }

        labelDataAccess.createLabel(label);
        return "Label created successfully.";
    }


    @Override
    public String editLabel(Label label) {

        if (label.getLabelName() == null || label.getLabelName().trim().isEmpty()) {
            return "Label name cannot be empty.";
        }

        if (label.getAmount() < 0) {
            return "Amount cannot be negative.";
        }

        if (label.getColor() == null || label.getColor().trim().isEmpty()) {
            return "Color cannot be empty.";
        }

        labelDataAccess.updateLabel(label);
        return "Label updated successfully.";
    }


    @Override
    public String assignLabelToAle(int id, int labelId) {
        AddLabelExpense ale = aleDataAccess.getAddLabelExpense(id);
        Label label = labelDataAccess.getLabelById(labelId);

        if (ale == null) {
            return "Expense not found.";
        }
        if (label == null) {
            return "Label not found.";
        }

        aleDataAccess.assignLabelExpense(id, label);
        return "Label added to expense successfully.";
    }

    // DELETE LABEL
    @Override
    public String deleteLabel(int labelId) {

        Label label = labelDataAccess.getLabelById(labelId);

        if (label == null) {
            return "Label does not exist.";
        }

        aleDataAccess.removeLabelFromAllEntries(labelId);
        labelDataAccess.deleteLabel(labelId);

        return "Label deleted successfully.";
    }

    // GET ALL LABELS FOR USER
    @Override
    public List<Label> getAllLabels(int userId) {
        return labelDataAccess.getAllLabelsByUser(userId);
    }
}
