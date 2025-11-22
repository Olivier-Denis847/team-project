package use_case.label;

import entity.Label;
import data_access.LabelDataAccess;
import data_access.ALEDataAccess;

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
        if (labelDataAccess.userHasLabelName(label.getLabelId(), label.getLabelName())) {
            return "Label with this name already exists";
        }else{
            labelDataAccess.createLabel(label);
            return "Label created successfully";
        }
    }

    @Override
    public String editLabel(Label label) {
        labelDataAccess.updateLabel(label);
        return "Label updated successfully";
    }

    @Override
    public String assignLabelToAle(int id, int labelId) {
        AddLabelExpense ale = aleDataAccess.getAddLabelExpense(id);
        Label label = labelDataAccess.getLabelById(labelId);

        if (ale == null){
            return "Expense not found";
        }
        if (label == null){
            return "Label not found";
        }
        aleDataAccess.assignLabelExpense(id, label);
        return "Label added to expense successfully";
    }

    @Override
    public String deleteLabel(int labelId) {
        Label lable =labelDataAccess.getLabelById(labelId);
        if (lable == null){
            return "Label does not exist";
        }
        aleDataAccess.removeLabelFromAllEntries(labelId);
        labelDataAccess.deleteLabel(labelId);
        return "Label deleted successfully";
    }

    @Override
    public List<Label> getAllLabels(int userId) {
        return labelDataAccess.db.values().stream()
                .filter(label -> label.getUserId() == userId)
                .toList();
    }
}
