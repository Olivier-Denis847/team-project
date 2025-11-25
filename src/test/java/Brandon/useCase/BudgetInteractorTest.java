package Brandon.useCase;

import entity.Budget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import use_case.budget.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BudgetInteractorTest {

    private BudgetDataAccessInterface dataAccess;
    private BudgetOutputBoundary presenter;
    private BudgetInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = mock(BudgetDataAccessInterface.class);
        presenter = mock(BudgetOutputBoundary.class);
        interactor = new BudgetInteractor(dataAccess, presenter);
    }

    @Test
    void execute_createsNewBudgetWhenNoneExists() {
        String monthKey = "01-2025";
        when(dataAccess.getBudgetForMonth(monthKey)).thenReturn(null);

        BudgetInputData input = new BudgetInputData(monthKey, 1000f, 250f);
        interactor.execute(input);

        // last saved Budget (after timestamp update)
        ArgumentCaptor<Budget> budgetCaptor = ArgumentCaptor.forClass(Budget.class);
        verify(dataAccess, atLeastOnce()).saveBudget(budgetCaptor.capture());
        Budget saved = budgetCaptor.getValue();

        assertEquals(monthKey, saved.getMonth());
        assertEquals(1000f, saved.getLimit());
        assertEquals(250f, saved.getTotalSpent());
        assertEquals(750f, saved.getRemaining(), 0.0001);
        assertNotNull(saved.getLastUpdated());
        assertFalse(saved.getLastUpdated().isBlank());

        // presenter called with correct output data
        ArgumentCaptor<BudgetOutputData> outCaptor = ArgumentCaptor.forClass(BudgetOutputData.class);
        verify(presenter).present(outCaptor.capture());
        BudgetOutputData out = outCaptor.getValue();

        assertEquals(monthKey, out.getMonth());
        assertEquals(1000f, out.getLimit());
        assertEquals(250f, out.getTotalSpent());
        assertEquals(750f, out.getRemaining(), 0.0001);
        assertTrue(out.getSuccess());
        assertEquals("Budget set successfully.", out.getMessage());
    }

    @Test
    void execute_updatesExistingBudget() {
        String monthKey = "02-2025";
        Budget existing = new Budget(monthKey);
        existing.setLimit(500f);
        existing.setTotalSpent(100f);

        when(dataAccess.getBudgetForMonth(monthKey)).thenReturn(existing);

        BudgetInputData input = new BudgetInputData(monthKey, 800f, 400f);
        interactor.execute(input);

        // same entity is updated
        assertEquals(800f, existing.getLimit());
        assertEquals(400f, existing.getTotalSpent());
        assertEquals(400f, existing.getRemaining(), 0.0001);
        assertNotNull(existing.getLastUpdated());
        assertFalse(existing.getLastUpdated().isBlank());

        verify(dataAccess, atLeastOnce()).saveBudget(existing);
        verify(presenter).present(any(BudgetOutputData.class));
    }
}