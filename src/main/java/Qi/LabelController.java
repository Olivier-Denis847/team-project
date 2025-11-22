package Qi;

import java.util.Date;
import java.util.List;

public class LabelController {

    private LabelUserCase userCase;

    public LabelController(LabelUserCase userCase) {
        this.userCase = userCase;
    }

    public String createLabel(String name, double amount, Date date, String description, String color, int userId) {
        Label label = new Label(0, name, date, color, userId, amount, description);
        return userCase.createLabel(label);
    }

    public String editLabel(Label label) {
        return userCase.editLabel(label);
    }

    public String assignLabelToAle(int id, int labelId) {
        return userCase.assignLabelToAle(id, labelId);
    }

    public String deleteLabel(int labelId) {
        return userCase.deleteLabel(labelId);
    }

    public List<Label> getAllLabels(int userId) {
        return userCase.getAllLabels(userId);
    }
}

