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

    @Override
    public boolean userHasLabelName(int userid, String labelName) {
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
