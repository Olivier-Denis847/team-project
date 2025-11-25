package Brandon.interfaceAdapter;

import interface_adapter.budget.BudgetController;
import use_case.budget.BudgetInputBoundary;
import use_case.budget.BudgetInputData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BudgetController.
 * Ensures it forwards input values correctly to the interactor.
 */
public class BudgetControllerTest {

    /**
     * Fake interactor that records the last received input.
     */
    private static class FakeInteractor implements BudgetInputBoundary {
        BudgetInputData received = null;

        @Override
        public void execute(BudgetInputData inputData) {
            this.received = inputData;
        }
    }

    @Test
    void testControllerForwardsInputToInteractor() {
        // Arrange
        FakeInteractor fake = new FakeInteractor();
        BudgetController controller = new BudgetController(fake);

        // Act
        controller.setBudget("03-2025", 400f, 150f);

        // Assert
        assertNotNull(fake.received, "Interactor should receive an input");
        assertEquals("03-2025", fake.received.getMonth());
        assertEquals(400f, fake.received.getLimit());
        assertEquals(150f, fake.received.getTotalSpent());
    }
}
