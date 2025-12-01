package Label;

import entity.Label;
import data_access.FinanceDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.label.ALEDataAccessInterface;
import use_case.label.AddLabelExpense;
import use_case.label.LabelDataAccessInterface;
import use_case.label.LabelUserCaseImp;

import java.util.ArrayList;
import java.util.List;

public class LabelUseCaseTest {

    private LabelUserCaseImp useCase;
    private StubLabelDataAccess labelDao;
    private StubALEDataAccess aleDao;

    @BeforeEach
    public void setUp() {
        // Reset the stubs before every test
        labelDao = new StubLabelDataAccess();
        aleDao = new StubALEDataAccess();
        useCase = new LabelUserCaseImp((LabelDataAccessInterface) labelDao, (ALEDataAccessInterface) aleDao);
    }

    // ==========================================
    // TEST: Create Label (All Branches)
    // ==========================================

    @Test
    public void testCreateLabel_Success() {
        Label label = new Label(0, "Rent", "Red", "Desc");
        String result = useCase.createLabel(label);
        Assertions.assertEquals("Label created successfully.", result);
        Assertions.assertTrue(labelDao.wasCreateCalled);
    }

    @Test
    public void testCreateLabel_Fail_EmptyName() {
        Label label = new Label(0, "", "Red", "Desc");
        String result = useCase.createLabel(label);
        Assertions.assertEquals("Label name cannot be empty.", result);
    }

    @Test
    public void testCreateLabel_Fail_NegativeAmount() {
        Label label = new Label(0, "Rent", "Red", "Desc");
        String result = useCase.createLabel(label);
        Assertions.assertEquals("Amount cannot be negative.", result);
    }

    @Test
    public void testCreateLabel_Fail_EmptyColor() {
        Label label = new Label(0, "Rent", "", "Desc");
        String result = useCase.createLabel(label);
        Assertions.assertEquals("Color cannot be empty.", result);
    }

    @Test
    public void testCreateLabel_Fail_DuplicateName() {
        Label label = new Label(0, "Rent", "Red", "Desc");
        labelDao.forceExists = true; // Simulate that label exists
        String result = useCase.createLabel(label);
        Assertions.assertEquals("Label with this name already exists.", result);
    }

    // ==========================================
    // TEST: Edit Label (All Branches)
    // ==========================================

    @Test
    public void testEditLabel_Success() {
        Label label = new Label(1, "Rent", "Red", "Desc");
        String result = useCase.editLabel(label);
        Assertions.assertEquals("Label updated successfully.", result);
        Assertions.assertTrue(labelDao.wasUpdateCalled);
    }

    @Test
    public void testEditLabel_Fail_Validation() {
        // We only need to test one fail condition here since the logic
        // mirrors createLabel, but testing one confirms the "if" works.
        Label label = new Label(1, null, "Red", "Desc");
        String result = useCase.editLabel(label);
        Assertions.assertEquals("Label name cannot be empty.", result);
    }

    // ==========================================
    // TEST: Delete Label (All Branches)
    // ==========================================

    @Test
    public void testDeleteLabel_Success() {
        labelDao.returnLabel = new Label(1, "Rent", "Red", "Desc");
        String result = useCase.deleteLabel(1);
        Assertions.assertEquals("Label deleted successfully.", result);
        Assertions.assertTrue(labelDao.wasDeleteCalled);
        Assertions.assertTrue(aleDao.wasRemoveCalled);
    }

    @Test
    public void testDeleteLabel_Fail_NotFound() {
        labelDao.returnLabel = null; // Simulate DB returning null
        String result = useCase.deleteLabel(99);
        Assertions.assertEquals("Label does not exist.", result);
    }

    // ==========================================
    // TEST: Assign Label to ALE (All Branches)
    // ==========================================

    @Test
    public void testAssignLabel_Success() {
        aleDao.returnAle = new AddLabelExpense(1, 100.0, true);
        labelDao.returnLabel = new Label(1, "Rent", "Red", "Desc");

        String result = useCase.assignLabelToAle(1, 1);
        Assertions.assertEquals("Label added to expense successfully.", result);
        Assertions.assertTrue(aleDao.wasAssignCalled);
    }

    @Test
    public void testAssignLabel_Fail_ExpenseNotFound() {
        aleDao.returnAle = null; // Simulate ALE not found
        labelDao.returnLabel = new Label(1, "Rent", "Red", "Desc");

        String result = useCase.assignLabelToAle(1, 1);
        Assertions.assertEquals("Expense not found.", result);
    }

    @Test
    public void testAssignLabel_Fail_LabelNotFound() {
        aleDao.returnAle = new AddLabelExpense(1, 100.0, true);
        labelDao.returnLabel = null; // Simulate Label not found

        String result = useCase.assignLabelToAle(1, 1);
        Assertions.assertEquals("Label not found.", result);
    }

    // ==========================================
    // TEST: Get All Labels
    // ==========================================
    @Test
    public void testGetAllLabels() {
        useCase.getAllLabels(1);
        Assertions.assertTrue(labelDao.wasGetAllCalled);
    }


    // =================================================================
    // STUB Classes (Fake Database) - Implements Interfaces for Testing
    // =================================================================

    class StubLabelDataAccess extends FinanceDataAccess implements LabelDataAccessInterface {
        boolean wasCreateCalled = false;
        boolean wasUpdateCalled = false;
        boolean wasDeleteCalled = false;
        boolean wasGetAllCalled = false;
        boolean forceExists = false;
        Label returnLabel = null;

        @Override
        public void updateLabel(Label label) { wasUpdateCalled = true; }

        @Override
        public void createLabel(Label label) { wasCreateCalled = true; }

        @Override
        public Label getLabelById(int labelId) { return returnLabel; }

        @Override
        public List<Label> getAllLabelsByUser(int userid) {
            wasGetAllCalled = true;
            return new ArrayList<>();
        }

        @Override
        public boolean labelExists(int userid, String labelName) { return forceExists; }

        @Override
        public void deleteLabel(int labelId) { wasDeleteCalled = true; }
    }

    static class StubALEDataAccess extends FinanceDataAccess implements ALEDataAccessInterface {
        boolean wasRemoveCalled = false;
        boolean wasAssignCalled = false;
        AddLabelExpense returnAle = null;

        @Override
        public AddLabelExpense getAddLabelExpense(int id) { return returnAle; }

        @Override
        public void removeLabelFromAllEntries(int labelId) { wasRemoveCalled = true; }

        @Override
        public void assignLabelExpense(int id, Label label) { wasAssignCalled = true; }
    }
}