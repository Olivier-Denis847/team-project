package use_case;

import entity.Label;

import java.util.List;

public interface LabelUserCase {
    String createLabel(Label label);
    String editLabel(Label label);
    String assignLabelToAle(int id, int labelId);
    String deleteLabel(int labelId);
    List<Label> getAllLabels(int userId);
}
