package use_case;

import entity.Label;

import java.util.List;

public interface LabelDataAccessInterface {
    void updateLabel(Label label);
    void createLabel(Label label);
    Label getLabelById(int labelId);

    // Updated signature to match the implementation
    List<Label> getAllLabelsByUser(int userid);

    // Added for validation
    boolean labelExists(int userid, String labelName);

    void deleteLabel(int labelId);
}