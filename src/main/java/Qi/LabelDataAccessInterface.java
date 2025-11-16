package Qi;

public interface LabelDataAccessInterface {
    void updateLabel(Label label);
    void createLabel(Label label);
    Label getLabelById(int labelId);
    boolean userHasLabelName(int userid, String labelName);
    void deleteLabel(int labelId);
}
