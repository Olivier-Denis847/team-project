package use_case.label;

import entity.Label;

import java.util.List;

public interface LabelUserCase {
    String createLabel(Label label);

    String editLabel(Label label);

    String assignLabelToAle(int id, int labelId);

    void assignLabelToExpense(int transactionId, Label label);

    // Remove a specific label from a transaction
    void removeLabelFromExpense(int transactionId, int labelId);

    String deleteLabel(int labelId);

    List<Label> getAllLabels(int userId);
}
