package data_access;

import entity.Label;
import use_case.label.LabelDataAccessInterface;

import java.util.*;

public class LabelDataAccess implements LabelDataAccessInterface {

    private int count = 1;
    public Map<Integer, Label> db = new HashMap<>();

    @Override
    public void updateLabel(Label label) {
        db.put(label.getLabelId(), label);
    }

    @Override
    public void createLabel(Label label) {
        label.setLabelId(count++);
        db.put(label.getLabelId(), label);
    }

    @Override
    public Label getLabelById(int id) {
        return db.get(id);
    }

    // FIXED: Returns a List (for the View)
    @Override
    public List<Label> getAllLabelsByUser(int userid) {
        return new ArrayList<>(db.values());
    }

    // NEW: Returns a boolean (for the Use Case validation)
    @Override
    public boolean labelExists(int userid, String labelName) {
        return db.values().stream()
                .anyMatch(l -> l.getLabelName().equalsIgnoreCase(labelName));
    }

    @Override
    public void deleteLabel(int labelId) {
        db.remove(labelId);
    }

    @Override
    public void assignLabelExpense(int transactionId, Label label) {
        // This is an in-memory implementation
        // For real persistence, this should be handled by FinanceDataAccess
        // This method is here for interface compliance
    }

    @Override
    public void removeLabelFromExpense(int transactionId, int labelId) {
        // This is an in-memory implementation
        // For real persistence, this should be handled by FinanceDataAccess
        // This method is here for interface compliance
    }
}
