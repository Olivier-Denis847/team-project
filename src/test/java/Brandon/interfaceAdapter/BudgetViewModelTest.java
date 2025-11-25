package Brandon.interfaceAdapter;

import interface_adapter.budget.BudgetViewModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BudgetViewModelTest {

    @Test
    void settersAndGettersWork() {
        BudgetViewModel vm = new BudgetViewModel();

        vm.setMonth("04-2025");
        vm.setLimit(200f);
        vm.setTotalSpent(50f);
        vm.setRemaining(150f);
        vm.setSuccess(true);
        vm.setMessage("saved");

        assertEquals("04-2025", vm.getMonth());
        assertTrue(vm.isSuccess());
        assertEquals("saved", vm.getMessage());
    }
}