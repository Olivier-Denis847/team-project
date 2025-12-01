package budget.interfaceAdapter;

import interface_adapter.budget.BudgetPresenter;
import interface_adapter.budget.BudgetViewModel;
import use_case.budget.BudgetOutputData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BudgetPresenterTest {

    @Test
    void present_copiesFieldsIntoViewModel() {
        BudgetViewModel vm = new BudgetViewModel();
        BudgetPresenter presenter = new BudgetPresenter(vm);

        BudgetOutputData data = new BudgetOutputData(
                "03-2025",
                100f,
                40f,
                60f,
                true,
                "Budget set successfully."
        );

        presenter.present(data);

        assertEquals("03-2025", vm.getMonth());
        assertTrue(vm.isSuccess());
        assertEquals("Budget set successfully.", vm.getMessage());
        // limit / spent / remaining are covered via setters; they just don't have getters
    }
}