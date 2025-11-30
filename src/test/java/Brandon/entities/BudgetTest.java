package Brandon.entities;

import entity.Budget;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {

    @Test
    void remainingAndStatusChangeWithSpending() {
        Budget b = new Budget("01-2025", 500, 340);
        b.setLimit(100f);

        b.setTotalSpent(40f);
        assertEquals(60f, b.getRemaining(), 0.0001);
        assertEquals("On track", b.getStatus());

        b.setTotalSpent(100f);
        assertEquals(0f, b.getRemaining(), 0.0001);
        assertEquals("Budget hit", b.getStatus());

        b.setTotalSpent(120f);
        assertEquals(-20f, b.getRemaining(), 0.0001);
        assertEquals("Over budget", b.getStatus());
    }

    @Test
    void notesAndLastUpdatedStoreValues() {
        Budget b = new Budget("02-2025", 800, 500);
        b.setNotes("some note");
        b.setLastUpdated("2025-02-01 10:00 EST");

        assertEquals("some note", b.getNotes());
        assertEquals("2025-02-01 10:00 EST", b.getLastUpdated());
    }
}