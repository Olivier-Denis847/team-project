package Qi;

public interface ALEDataAccessInterface {
    AddLabelExpense getAddLabelExpense(int id);
    void removeLabelFromAllEntries(int labelId);
    void assignLabelExpense(int id, Label label);
}