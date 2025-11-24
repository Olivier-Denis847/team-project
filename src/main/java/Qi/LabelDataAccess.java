package Qi;

import java.util.*;
import java.util.stream.Collectors;

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
        return db.values().stream()
                .filter(l -> l.getUserId() == userid)
                .collect(Collectors.toList());
    }

    // NEW: Returns a boolean (for the Use Case validation)
    @Override
    public boolean labelExists(int userid, String labelName) {
        return db.values().stream()
                .anyMatch(l ->
                        l.getUserId() == userid &&
                                l.getLabelName().equalsIgnoreCase(labelName)
                );
    }

    @Override
    public void deleteLabel(int labelId) {
        db.remove(labelId);
    }
}
