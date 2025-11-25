package use_case.label;

import entity.Label;

public interface ALEDataAccessInterface {
    AddLabelExpense getAddLabelExpense(int id);
    void removeLabelFromAllEntries(int labelId);
    void assignLabelExpense(int id, Label label);
}