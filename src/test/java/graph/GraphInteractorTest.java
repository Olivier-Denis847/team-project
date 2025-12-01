package graph;

import entity.Label;
import entity.Transaction;
import org.junit.jupiter.api.Test;
import use_case.graph.*;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GraphInteractorTest {

    /*
     * =========================
     * Test DAO Stub
     * =========================
     */
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
            if (expenses != null)
                all.addAll(expenses);
            if (incomes != null)
                all.addAll(incomes);
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

    /*
     * =========================
     * Presenter Stub
     * =========================
     */
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

    /*
     * =========================
     * Special Transaction for td == null
     * =========================
     */
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
            if (callCount == 1)
                return null; // triggers td == null branch
            return nonNullDate;
        }
    }

    /*
     * =========================
     * Helpers
     * =========================
     */
    private static Label createLabel(String name, String color) {
        return new Label(1, name, color, "desc");
    }

    private static Date createDate(int year, int monthZeroBased, int dayOfMonth) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthZeroBased);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return cal.getTime();
    }

    /*
     * =========================
     * Test getSelectedRange with null
     * =========================
     */
    @Test
    void getSelectedRange_nullReturnsDay() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Method method = GraphInteractor.class.getDeclaredMethod("getSelectedRange", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(interactor, (String) null);
        assertEquals("Day", result);
    }

    /*
     * =========================
     * Test getSelectedRange with non-null
     * =========================
     */
    @Test
    void getSelectedRange_nonNullReturnsInput() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Method method = GraphInteractor.class.getDeclaredMethod("getSelectedRange", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(interactor, "Month");
        assertEquals("Month", result);
    }

    /*
     * =========================
     * Test getSelectedType with null
     * =========================
     */
    @Test
    void getSelectedType_nullReturnsExpense() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Method method = GraphInteractor.class.getDeclaredMethod("getSelectedType", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(interactor, (String) null);
        assertEquals("Expense", result);
    }

    /*
     * =========================
     * Test getSelectedType with non-null
     * =========================
     */
    @Test
    void getSelectedType_nonNullReturnsInput() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Method method = GraphInteractor.class.getDeclaredMethod("getSelectedType", String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(interactor, "Income");
        assertEquals("Income", result);
    }

    /*
     * =========================
     * Test getTransactionsForType - Expense
     * =========================
     */
    @Test
    void getTransactionsForType_expenseReturnsExpenses() throws Exception {
        Label food = createLabel("Food", "#FFB3BA");
        Transaction exp = new Transaction(1L, 50f, List.of(food), "lunch",
                createDate(2025, 0, 1), "expense");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(exp), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Method method = GraphInteractor.class.getDeclaredMethod("getTransactionsForType", String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Transaction> result = (List<Transaction>) method.invoke(interactor, "Expense");
        assertEquals(1, result.size());
        assertEquals(50f, result.get(0).getAmount());
    }

    /*
     * =========================
     * Test getTransactionsForType - Income
     * =========================
     */
    @Test
    void getTransactionsForType_incomeReturnsIncomes() throws Exception {
        Label salary = createLabel("Salary", "#BAE1FF");
        Transaction inc = new Transaction(1L, 1000f, List.of(salary), "paycheck",
                createDate(2025, 0, 1), "income");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of(inc));
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Method method = GraphInteractor.class.getDeclaredMethod("getTransactionsForType", String.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<Transaction> result = (List<Transaction>) method.invoke(interactor, "Income");
        assertEquals(1, result.size());
        assertEquals(1000f, result.get(0).getAmount());
    }

    /*
     * =========================
     * DAY RANGE — Full Branch Matrix
     * =========================
     */
    @Test
    void execute_dayRange_hitsAllYearMonthBranches() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int day = 10;

        Label food = createLabel("Food", "#FFB3BA");

        // (T, T)
        Transaction tCurrent = new Transaction(
                1L, 50f, List.of(food), "current",
                createDate(year, month, day), "expense");

        // (T, F)
        Transaction tOtherMonth = new Transaction(
                2L, 20f, List.of(food), "other-month",
                createDate(year, (month + 1) % 12, day), "expense");

        // (F, T)
        Transaction tOtherYear = new Transaction(
                3L, 30f, List.of(food), "other-year",
                createDate(year - 1, month, day), "expense");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(tCurrent, tOtherMonth, tOtherYear), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData(null, null));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertEquals("Day", out.getSelectedRange());
        assertEquals("Expense", out.getSelectedType());
        assertEquals(50f, out.getBar().get(day));
        assertEquals(50f, out.getPie().get("Food"));
        assertEquals("#FFB3BA", out.getLabelColors().get("Food"));
    }

    /*
     * =========================
     * MONTH RANGE — addToBar ELSE branch
     * =========================
     */
    @Test
    void execute_monthRange_hitsAddToBarElseAndYearFalse() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = 1; // Feb

        Label salary = createLabel("Salary", "#BAE1FF");

        Transaction t1 = new Transaction(
                1L, 100f, List.of(salary), "s1",
                createDate(year, month, 5), "income");

        Transaction t2 = new Transaction(
                2L, 50f, List.of(salary), "s2",
                createDate(year, month, 12), "income");

        Transaction t3 = new Transaction(
                3L, 999f, List.of(salary), "old",
                createDate(year - 1, month, 5), "income");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of(t1, t2, t3));

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("Month", "Income"));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertEquals(150f, out.getBar().get(month + 1));
        assertEquals(150f, out.getPie().get("Salary"));
        assertEquals("#BAE1FF", out.getLabelColors().get("Salary"));
    }

    /*
     * =========================
     * YEAR RANGE — labels null & empty
     * =========================
     */
    @Test
    void execute_yearRange_skipsNullAndEmptyLabels() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        Transaction t1 = new Transaction(
                1L, 10f, null, "no-label",
                createDate(year - 2, 0, 1), "expense");

        Transaction t2 = new Transaction(
                2L, 20f, new ArrayList<>(), "empty-label",
                createDate(year - 1, 0, 1), "expense");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(t1, t2), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("Year", "Expense"));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertTrue(out.getPie().isEmpty());
        assertTrue(out.getLabelColors().isEmpty());
    }

    /*
     * =========================
     * td == null AND minYear fallback
     * =========================
     */
    @Test
    void execute_yearRange_tdNullAndFallbackYear() {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        Label misc = createLabel("Misc", "#E0BBE4");

        Transaction t = new ChangingDateTransaction(
                5f, List.of(misc),
                createDate(year, 0, 1));

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(t), List.of());

        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        interactor.execute(new GraphInputData("Year", "Expense"));

        GraphOutputData out = presenter.getLastGraphOutput();
        assertEquals(5f, out.getBar().get(year));
        assertEquals(5f, out.getPie().get("Misc"));
        assertEquals("#E0BBE4", out.getLabelColors().get("Misc"));
    }

    /*
     * =========================
     * Test addToPie with existing label (ELSE branch)
     * =========================
     */
    @Test
    void addToPie_hitsElseBranchForExistingLabel() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Label food = createLabel("Food", "#FFB3BA");
        Map<String, Float> pie = new HashMap<>();
        Map<String, String> labelColors = new HashMap<>();

        Transaction t1 = new Transaction(1L, 10f, List.of(food), "t1", new Date(), "expense");
        Transaction t2 = new Transaction(2L, 15f, List.of(food), "t2", new Date(), "expense");

        Method method = GraphInteractor.class.getDeclaredMethod(
                "addToPie", Map.class, Map.class, List.class, Transaction.class);
        method.setAccessible(true);

        // First call - IF branch (label not in pie)
        method.invoke(interactor, pie, labelColors, t1.getLabels(), t1);
        assertEquals(10f, pie.get("Food"));
        assertEquals("#FFB3BA", labelColors.get("Food"));

        // Second call - ELSE branch (label already in pie)
        method.invoke(interactor, pie, labelColors, t2.getLabels(), t2);
        assertEquals(25f, pie.get("Food"));
    }

    /*
     * =========================
     * Test addToPie with color already stored (ELSE for labelColors)
     * =========================
     */
    @Test
    void addToPie_colorAlreadyStored() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Label food = createLabel("Food", "#FFB3BA");
        Map<String, Float> pie = new HashMap<>();
        Map<String, String> labelColors = new HashMap<>();
        labelColors.put("Food", "#FFB3BA"); // Pre-populate color

        Transaction t = new Transaction(1L, 10f, List.of(food), "t", new Date(), "expense");

        Method method = GraphInteractor.class.getDeclaredMethod(
                "addToPie", Map.class, Map.class, List.class, Transaction.class);
        method.setAccessible(true);

        method.invoke(interactor, pie, labelColors, t.getLabels(), t);
        assertEquals(10f, pie.get("Food"));
        assertEquals("#FFB3BA", labelColors.get("Food"));
        assertEquals(1, labelColors.size()); // Color not duplicated
    }

    /*
     * =========================
     * DIRECT addToBar IF + ELSE
     * =========================
     */
    @Test
    void addToBar_hitsIfAndElseViaReflection() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Map<Integer, Float> bar = new HashMap<>();

        Label test = createLabel("Test", "#BAFFC9");
        Transaction t = new Transaction(
                1L, 7.5f, List.of(test),
                "note", new Date(), "expense");

        Method m = GraphInteractor.class.getDeclaredMethod(
                "addToBar", Map.class, int.class, Transaction.class);

        m.setAccessible(true);

        // IF branch (key not in bar)
        m.invoke(interactor, bar, 3, t);
        assertEquals(7.5f, bar.get(3));

        // ELSE branch (key already in bar)
        m.invoke(interactor, bar, 3, t);
        assertEquals(15f, bar.get(3));
    }

    /*
     * =========================
     * Test prefillBar Day range
     * =========================
     */
    @Test
    void prefillBar_dayRange() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Map<Integer, Float> bar = new HashMap<>();
        Calendar nowCal = Calendar.getInstance();
        int nowYear = nowCal.get(Calendar.YEAR);

        Method method = GraphInteractor.class.getDeclaredMethod(
                "prefillBar", Map.class, String.class, List.class, Calendar.class, int.class);
        method.setAccessible(true);

        method.invoke(interactor, bar, "Day", List.of(), nowCal, nowYear);

        int daysInMonth = nowCal.getActualMaximum(Calendar.DAY_OF_MONTH);
        assertEquals(daysInMonth, bar.size());
        assertEquals(0f, bar.get(1));
    }

    /*
     * =========================
     * Test prefillBar Month range
     * =========================
     */
    @Test
    void prefillBar_monthRange() throws Exception {
        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Map<Integer, Float> bar = new HashMap<>();
        Calendar nowCal = Calendar.getInstance();
        int nowYear = nowCal.get(Calendar.YEAR);

        Method method = GraphInteractor.class.getDeclaredMethod(
                "prefillBar", Map.class, String.class, List.class, Calendar.class, int.class);
        method.setAccessible(true);

        method.invoke(interactor, bar, "Month", List.of(), nowCal, nowYear);

        assertEquals(12, bar.size());
        assertEquals(0f, bar.get(1));
        assertEquals(0f, bar.get(12));
    }

    /*
     * =========================
     * Test prefillBar Year range with transactions
     * =========================
     */
    @Test
    void prefillBar_yearRangeWithTransactions() throws Exception {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        Label misc = createLabel("Misc", "#E0BBE4");
        Transaction oldT = new Transaction(1L, 10f, List.of(misc), "old",
                createDate(year - 3, 0, 1), "expense");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(oldT), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Map<Integer, Float> bar = new HashMap<>();
        Calendar nowCal = Calendar.getInstance();
        int nowYear = nowCal.get(Calendar.YEAR);

        Method method = GraphInteractor.class.getDeclaredMethod(
                "prefillBar", Map.class, String.class, List.class, Calendar.class, int.class);
        method.setAccessible(true);

        method.invoke(interactor, bar, "Year", List.of(oldT), nowCal, nowYear);

        assertTrue(bar.containsKey(year - 3));
        assertTrue(bar.containsKey(year));
        assertEquals(0f, bar.get(year - 3));
        assertEquals(0f, bar.get(year));
    }

    /*
     * =========================
     * Test fillBarAndPie - Year range (ELSE path)
     * =========================
     */
    @Test
    void fillBarAndPie_yearRange() throws Exception {
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);

        Label travel = createLabel("Travel", "#FFD9B3");
        Transaction t = new Transaction(1L, 200f, List.of(travel), "trip",
                createDate(year - 1, 5, 15), "expense");

        TestGraphDataAccess dao = new TestGraphDataAccess(List.of(t), List.of());
        CapturingGraphPresenter presenter = new CapturingGraphPresenter();
        GraphInteractor interactor = new GraphInteractor(dao, presenter);

        Map<Integer, Float> bar = new HashMap<>();
        bar.put(year - 1, 0f);
        Map<String, Float> pie = new HashMap<>();
        Map<String, String> labelColors = new HashMap<>();

        Method method = GraphInteractor.class.getDeclaredMethod(
                "fillBarAndPie", List.class, String.class, Map.class, Map.class, Map.class, int.class, int.class);
        method.setAccessible(true);

        method.invoke(interactor, List.of(t), "Year", bar, pie, labelColors, year, 0);

        assertEquals(200f, bar.get(year - 1));
        assertEquals(200f, pie.get("Travel"));
        assertEquals("#FFD9B3", labelColors.get("Travel"));
    }
}
