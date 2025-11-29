package graph;

import entity.Label;
import entity.Transaction;
import org.junit.jupiter.api.Test;
import use_case.graph.*;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphInteractorTest {

    /* =========================
       Test DAO Stub
       ========================= */
    private static class TestGraphDataAccess implements GraphDataAccessInterface {

        private final List<Transaction> expenses;
        private final List<Transaction> incomes;

        private String savedRange;
        private String savedType;

        TestGraphDataAccess(List<Transaction> expenses, List<Transaction> incomes) {
            this.expenses = expenses;
            this.incomes = incomes;
        }

        @Override
        public List<Transaction> getAllEntries() {
            List<Transaction> all = new ArrayList<>();
            if (expenses != null) all.addAll(expenses);
            if (incomes != null) all.addAll(incomes);
            return all;
        }

        @Override
        public void saveGraphRange(String lineGraphRange) {
            this.savedRange = lineGraphRange;
        }

        @Override
        public void saveGraphType(String type) {
            this.savedType = type;
        }

        @Override
        public String getRange() {
            return savedRange;
        }

        @Override
        public String getType() {
            return savedType;
        }

        @Override
        public List<Transaction> getExpenses() {
            return expenses == null ? Collections.emptyList() : expenses;
        }

        @Override
        public List<Transaction> getIncomes() {
            return incomes == null ? Collections.emptyList() : incomes;
        }
    }

    /* =========================
       Presenter Stub
       ========================= */
    private static class CapturingGraphPresenter implements GraphOutputBoundary {

        private GraphOutputData lastGraphOutput;
        private GraphOutputData lastAlertOutput;

        @Override
        public void prepareGraph(GraphOutputData graphOutputData) {
            this.lastGraphOutput = graphOutputData;
        }

        @Override
        public void prepareAlert(GraphOutputData graphOutputData) {
            this.lastAlertOutput = graphOutputData;
        }

        GraphOutputData getLastGraphOutput() {
            return lastGraphOutput;
        }

        GraphOutputData getLastAlertOutput() {
            return lastAlertOutput;
        }
    }

    /* =========================
       Special Transaction for td == null
       ========================= */
    private static class ChangingDateTransaction extends Transaction {
        private int callCount = 0;
        private final Date nonNullDate;

        ChangingDateTransaction(float amount, List<Label> labels, Date nonNullDate) {
            super(999L, amount, labels, "changing-date", nonNullDate, "expense");
            this.nonNullDate = nonNullDate;
        }

        @Override
        public Date getDate() {
            callCount++;
            if (callCount == 1) return null;   // triggers td == null branch
            return nonNullDate;
        }
    }

    /* =========================
       Helpers
       ========================= */
    private static Label createLabel(String name) {
        return new Label(1, name, "color", 1, 0.0, "desc");
    }

    private static Date createDate(int year, int monthZeroBased, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthZeroBased);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return cal.getTime();
    }

    /* =========================
       DAY RANGE — Full Branch Matrix
       ========================= */
    @Test
    void execute_dayRange_hitsAllYearMonthBranches() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = 10;

        Label food = createLabel("Food");

        // (T, T)
        Transaction tCurrent = new Transaction(
                1L, 50f, List.of(food), "current",
                createDate(year, month, day), "expense"
        );

        // (T, F)
        Transaction tOtherMonth = new Transaction(
                2L, 20f, List.of(food), "other-month",
                createDate(year, (month + 1) % 12, day), "expense"
        );

        // (F, T)
        Transaction tOtherYear = new Transaction(
                3L, 30f, List.of(food), "other-year",
                createDate(year - 1, month, day), "expense"
        );

        TestGraphDataAccess dao =
                new TestGraphDataAccess(List.of(tCurrent, tOtherMonth, tOtherYear), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData(null, null));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertEquals("Day", out.getSelectedRange());
        assertEquals("Expense", out.getSelectedType());
        assertEquals(50f, out.getBar().get(day));
        assertEquals(50f, out.getPie().get("Food"));
    }

    /* =========================
       MONTH RANGE — addToBar ELSE branch
       ========================= */
    @Test
    void execute_monthRange_hitsAddToBarElseAndYearFalse() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = 1; // Feb

        Label salary = createLabel("Salary");

        Transaction t1 = new Transaction(
                1L, 100f, List.of(salary), "s1",
                createDate(year, month, 5), "income"
        );

        Transaction t2 = new Transaction(
                2L, 50f, List.of(salary), "s2",
                createDate(year, month, 12), "income"
        );

        Transaction t3 = new Transaction(
                3L, 999f, List.of(salary), "old",
                createDate(year - 1, month, 5), "income"
        );

        TestGraphDataAccess dao =
                new TestGraphDataAccess(List.of(), List.of(t1, t2, t3));

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("Month", "Income"));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertEquals(150f, out.getBar().get(month + 1));
        assertEquals(150f, out.getPie().get("Salary"));
    }

    /* =========================
       YEAR RANGE — labels null & empty
       ========================= */
    @Test
    void execute_yearRange_skipsNullAndEmptyLabels() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        Transaction t1 = new Transaction(
                1L, 10f, null, "no-label",
                createDate(year - 2, 0, 1), "expense"
        );

        Transaction t2 = new Transaction(
                2L, 20f, new ArrayList<>(), "empty-label",
                createDate(year - 1, 0, 1), "expense"
        );

        TestGraphDataAccess dao =
                new TestGraphDataAccess(List.of(t1, t2), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("Year", "Expense"));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertTrue(out.getPie().isEmpty());
    }

    /* =========================
       td == null AND minYear fallback
       ========================= */
    @Test
    void execute_yearRange_tdNullAndFallbackYear() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        Label misc = createLabel("Misc");

        Transaction t = new ChangingDateTransaction(
                5f, List.of(misc),
                createDate(year, 0, 1)
        );

        TestGraphDataAccess dao =
                new TestGraphDataAccess(List.of(t), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("Year", "Expense"));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertEquals(5f, out.getBar().get(year));
        assertEquals(5f, out.getPie().get("Misc"));
    }

    /* =========================
       Empty Range & Type
       ========================= */
    @Test
    void execute_emptyRangeAndType() {
        TestGraphDataAccess dao =
                new TestGraphDataAccess(List.of(), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("", ""));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertNotNull(out.getSelectedRange());
        assertNotNull(out.getSelectedType());
    }

    /* =========================
       DIRECT addToBar IF + ELSE
       ========================= */
    @Test
    void addToBar_hitsIfAndElseViaReflection() throws Exception {
        TestGraphDataAccess dao =
                new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Map<Integer, Float> bar = new HashMap<>();

        Transaction t = new Transaction(
                1L, 7.5f, List.of(createLabel("Test")),
                "note", new Date(), "expense"
        );

        Method m = GraphInteractor.class.getDeclaredMethod(
                "addToBar", Map.class, int.class, Transaction.class);

        m.setAccessible(true);

        // IF branch
        m.invoke(interactor, bar, 3, t);
        assertEquals(7.5f, bar.get(3));

        // ELSE branch
        m.invoke(interactor, bar, 3, t);
        assertEquals(15f, bar.get(3));
    }
}
