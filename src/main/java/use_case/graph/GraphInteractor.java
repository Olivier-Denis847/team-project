package use_case.graph;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Entity.Transaction;
import Entity.Label;

public class GraphInteractor implements GraphInputBoundary {
    private final GraphDataAccessInterface dataAccessObject;
    private final GraphOutputBoundary graphPresenter;

    public GraphInteractor(GraphDataAccessInterface dataAccessInterface, GraphOutputBoundary graphOutputBoundary) {
        this.graphPresenter = graphOutputBoundary;
        this.dataAccessObject = dataAccessInterface;
    }

    @Override
    public void execute(GraphInputData graphInputData) {
        String selectedRange = graphInputData.getRange();
        String selectedType = graphInputData.getTransactionType();
        Map<Integer, Float> bar = new HashMap<>();
        Map<String, Float> pie = new HashMap<>();
        List<String> alerts = new ArrayList<>();

        // initalize to Day on first load
        if (selectedRange == null)
            selectedRange = "Day";
        if (selectedType == null)
            selectedType = "Expense";

        List<Transaction> transactions = selectedType.equalsIgnoreCase("Expense") ? dataAccessObject.getExpenses()
                : dataAccessObject.getIncomes();

        Date now = new Date();
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        int nowYear = nowCal.get(Calendar.YEAR);
        int nowMonth = nowCal.get(Calendar.MONTH);

        // Prefill the bar map with all possible keys initialized to 0
        if (selectedRange.equalsIgnoreCase("Day")) {
            int daysInMonth = nowCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int d = 1; d <= daysInMonth; d++) {
                bar.put(d, 0f);
            }
        } else if (selectedRange.equalsIgnoreCase("Month")) {
            for (int m = 0; m < 12; m++) {
                bar.put(m, 0f);
            }
        } else { // Year
            int minYear = Integer.MAX_VALUE;
            for (Transaction t : transactions) {
                Date td = t.getDate();
                if (td == null)
                    continue;
                Calendar cc = Calendar.getInstance();
                cc.setTime(td);
                minYear = Math.min(minYear, cc.get(Calendar.YEAR));
            }
            if (minYear == Integer.MAX_VALUE)
                minYear = nowYear;
            for (int y = minYear; y <= nowYear; y++) {
                bar.put(y, 0f);
            }
        }

        for (Transaction transaction : transactions) {
            Date t = transaction.getDate();
            Calendar c = Calendar.getInstance();
            c.setTime(t);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            List<Label> labels = transaction.getLabels();

            if (selectedRange.equals("Day")) {
                if (year == nowYear && month == nowMonth) {
                    addToBar(bar, day, transaction);
                    addToPie(pie, labels, transaction);
                }
            }

            else if (selectedRange.equals("Month")) {
                if (year == nowYear) {
                    addToBar(bar, month, transaction);
                    addToPie(pie, labels, transaction);
                }
            }

            else { // Year
                addToBar(bar, year, transaction);
                addToPie(pie, labels, transaction);
            }

        }

        /* package into output data and prepare graph */
        GraphOutputData outputData = new GraphOutputData(
                selectedRange,
                selectedType,
                bar,
                pie,
                alerts);
        graphPresenter.prepareGraph(outputData);
    }

    /**
     * helper method to process data into pie map
     * 
     * @param pie
     * @param labels
     * @param t
     */
    private void addToPie(Map<String, Float> pie, List<Label> labels, Transaction t) {
        if (labels == null || labels.isEmpty())
            return;
        for (Label label : labels) {
            String labelName = label.getLabelName();
            if (!pie.containsKey(labelName))
                pie.put(labelName, t.getAmount());
            else
                pie.put(labelName, pie.get(labelName) + t.getAmount());
        }
    }

    /**
     * helper method to process data into bar map
     * 
     * @param bar
     * @param range
     * @param t
     */
    private void addToBar(Map<Integer, Float> bar, int range, Transaction t) {
        if (!bar.containsKey(range))
            bar.put(range, t.getAmount());
        else
            bar.put(range, bar.get(range) + t.getAmount());
    }
}
