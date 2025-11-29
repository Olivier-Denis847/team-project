package data_access;

import entity.Budget;
import entity.Label;
import entity.Transaction;
import use_case.add_transaction.TransactionDataAccessInterface;
import use_case.graph.GraphDataAccessInterface;
import use_case.label.ALEDataAccessInterface;
import use_case.label.AddLabelExpense;
import use_case.label.LabelDataAccessInterface;
import use_case.budget.SetBudgetDataAccessInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * FinanceDataAccess implements the different data access interfaces for this application
 * and persists data into three JSON files:
 *
 * 1) TransactionData.JSON
 *    [
 *      {
 *        "id": 1,
 *        "amount": 10.5,
 *        "type": "expense",
 *        "note": "Coffee",
 *        "date": "1732752000000",
 *        "labelIds": [1, 2]
 *      },
 *      ...
 *    ]
 *
 * 2) LabelData.JSON
 *    (reserved for future label persistence)
 *
 * 3) BudgetData.JSON
 *    {
 *      "budgets": [
 *        {
 *          "budgetID": 1,
 *          "month": "2025-11",
 *          "limit": 1000.0,
 *          "totalSpent": 250.0
 *        }
 *      ],
 *      "graphRange": "Month",
 *      "graphType": "Expense"
 *    }
 */
public class FinanceDataAccess implements
        TransactionDataAccessInterface,
        SetBudgetDataAccessInterface,
        GraphDataAccessInterface,
        ALEDataAccessInterface,
        LabelDataAccessInterface {

    private final File transactionFile;
    private final File labelFile;
    private final File budgetFile;

    /**
     * Default constructor: uses fixed file names in the working directory.
     */
    public FinanceDataAccess() {
        this.transactionFile = new File("TransactionData.JSON");
        this.labelFile = new File("LabelData.JSON");
        this.budgetFile = new File("BudgetData.JSON");
    }

    /**
     * Constructor that allows custom file paths (useful for testing).
     */
    public FinanceDataAccess(String transactionPath, String labelPath, String budgetPath) {
        this.transactionFile = new File(transactionPath);
        this.labelFile = new File(labelPath);
        this.budgetFile = new File(budgetPath);
    }

    // ===============================
    // Transaction persistence
    // ===============================

    /**
     * Save a transaction by appending it to the list and rewriting the JSON file.
     *
     * @param transaction the transaction to save
     */
    @Override
    public synchronized void save(Transaction transaction) {
        List<Transaction> transactions = getAll();
        transactions.add(transaction);
        writeTransactions(transactions);
    }

    /**
     * Read all transactions from TransactionData.JSON.
     *
     * @return list of all stored transactions
     */
    @Override
    public synchronized List<Transaction> getAll() {
        List<Transaction> result = new ArrayList<>();

        if (!transactionFile.exists()) {
            return result;
        }

        String content = readFile(transactionFile);
        if (content == null) {
            return result;
        }

        content = content.trim();
        if (content.isEmpty()) {
            return result;
        }

        try {
            JSONArray arr = new JSONArray(content);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                long id = obj.optLong("id");
                float amount = (float) obj.optDouble("amount", 0.0);
                String type = obj.optString("type", null);
                String note = obj.optString("note", null);

                Date date = null;
                if (obj.has("date") && !obj.isNull("date")) {
                    String dateString = obj.get("date").toString();
                    try {
                        long millis = Long.parseLong(dateString);
                        date = new Date(millis);
                    } catch (NumberFormatException ignored) {
                        // leave date as null if parsing fails
                    }
                }

                // For now, we ignore label IDs when reconstructing the Transaction.
                // The labels list is left empty; label-related methods are handled elsewhere.
                // TODO: reconstruct LABELS using labelData.json files when implemented
                List<Label> labels = new ArrayList<>();

                Transaction t = new Transaction(id, amount, labels, note, date, type);
                result.add(t);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse TransactionData.JSON", e);
        }

        return result;
    }

    /**
     * Convenience method required by TransactionDataAccessInterface.
     *
     * @return all transactions
     */
    @Override
    public List<Transaction> getTransactions() {
        return getAll();
    }

    /**
     * Helper to write the list of transactions to TransactionData.JSON.
     * Each transaction saves the IDs of its labels in the "labelIds" field.
     *
     * @param transactions the transactions to persist
     */
    private synchronized void writeTransactions(List<Transaction> transactions) {
        JSONArray arr = new JSONArray();

        for (Transaction t : transactions) {
            JSONObject obj = new JSONObject();
            obj.put("id", t.getId());
            obj.put("amount", t.getAmount());
            obj.put("type", t.getType() == null ? JSONObject.NULL : t.getType());
            obj.put("note", t.getNote() == null ? JSONObject.NULL : t.getNote());

            if (t.getDate() != null) {
                obj.put("date", String.valueOf(t.getDate().getTime()));
            } else {
                obj.put("date", JSONObject.NULL);
            }

            // Save only label IDs, not full label objects.
            JSONArray labelIds = new JSONArray();
            if (t.getLabels() != null) {
                for (Label label : t.getLabels()) {
                    labelIds.put(label.getLabelId());
                }
            }
            obj.put("labelIds", labelIds);

            arr.put(obj);
        }

        writeFile(transactionFile, arr.toString(4));
    }

    // ===============================
    // GraphDataAccessInterface methods
    // ===============================

    /**
     * get all past entries
     *
     * @return all past entries in an array
     */
    @Override
    public List<Transaction> getAllEntries() {
        return getAll();
    }

    /**
     * save lineGraphRange data for next use
     *
     * @param lineGraphRange the range data for line graph
     */
    @Override
    public synchronized void saveGraphRange(String lineGraphRange) {
        JSONObject root = readBudgetRoot();
        root.put("graphRange", lineGraphRange);
        writeBudgetRoot(root);
    }

    /**
     * save the type of entry to graph
     *
     * @param type the type of entry
     */
    @Override
    public synchronized void saveGraphType(String type) {
        JSONObject root = readBudgetRoot();
        root.put("graphType", type);
        writeBudgetRoot(root);
    }

    /**
     * get range data from database
     *
     * @return the graph range data in database, null if file not found
     */
    @Override
    public synchronized String getRange() {
        JSONObject root = readBudgetRoot();
        if (!root.has("graphRange") || root.isNull("graphRange")) {
            return null;
        }
        return root.optString("graphRange", null);
    }

    /**
     * get type data from database
     *
     * @return the pie graph range data in database, null if file not found
     */
    @Override
    public synchronized String getType() {
        JSONObject root = readBudgetRoot();
        if (!root.has("graphType") || root.isNull("graphType")) {
            return null;
        }
        return root.optString("graphType", null);
    }

    /**
     * Get only expense transactions from the data source.
     *
     * @return list of expense transactions
     */
    @Override
    public List<Transaction> getExpenses() {
        List<Transaction> all = getAll();
        List<Transaction> expenses = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getType() != null && "expense".equalsIgnoreCase(t.getType())) {
                expenses.add(t);
            }
        }
        return expenses;
    }

    /**
     * Get only income transactions from the data source.
     *
     * @return list of income transactions
     */
    @Override
    public List<Transaction> getIncomes() {
        List<Transaction> all = getAll();
        List<Transaction> incomes = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getType() != null && "income".equalsIgnoreCase(t.getType())) {
                incomes.add(t);
            }
        }
        return incomes;
    }

    // ===============================
    // SetBudgetDataAccessInterface
    // ===============================

    /**
     * @param month the month of interest (e.g., "2025-11")
     * @return the Budget for that month, or null if none exists
     */
    @Override
    public synchronized Budget getBudgetForMonth(String month) {
        JSONObject root = readBudgetRoot();
        if (!root.has("budgets") || root.isNull("budgets")) {
            return null;
        }

        JSONArray arr = root.getJSONArray("budgets");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String m = obj.optString("month", null);
            if (month.equals(m)) {
                Budget budget = new Budget(month);
                budget.setBudgetID(obj.optInt("budgetID", 0));
                budget.setLimit((float) obj.optDouble("limit", 0.0));
                budget.setTotalSpent((float) obj.optDouble("totalSpent", 0.0));
                return budget;
            }
        }
        return null;
    }

    /**
     * Save or update a budget. If a budget for the same month already exists, it is updated.
     *
     * @param budget the budget to save
     */
    @Override
    public synchronized void saveBudget(Budget budget) {
        JSONObject root = readBudgetRoot();
        JSONArray arr = root.optJSONArray("budgets");
        if (arr == null) {
            arr = new JSONArray();
        }

        // Ensure budgetID is set; simple "max existing + 1" strategy if needed.
        if (budget.getBudgetID() == 0) {
            int nextId = 1;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int existingId = obj.optInt("budgetID", 0);
                if (existingId >= nextId) {
                    nextId = existingId + 1;
                }
            }
            budget.setBudgetID(nextId);
        }

        boolean updated = false;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String m = obj.optString("month", null);
            if (budget.getMonth().equals(m)) {
                obj.put("budgetID", budget.getBudgetID());
                obj.put("month", budget.getMonth());
                obj.put("limit", budget.getLimit());
                obj.put("totalSpent", budget.getTotalSpent());
                updated = true;
                break;
            }
        }

        if (!updated) {
            JSONObject obj = new JSONObject();
            obj.put("budgetID", budget.getBudgetID());
            obj.put("month", budget.getMonth());
            obj.put("limit", budget.getLimit());
            obj.put("totalSpent", budget.getTotalSpent());
            arr.put(obj);
        }

        root.put("budgets", arr);
        writeBudgetRoot(root);
    }

    // ===============================
    // Label-related interfaces (TODOs)
    // ===============================
    // TODO: Proper label persistence should be implemented here in a later iteration.

    @Override
    public AddLabelExpense getAddLabelExpense(int id) {
        // TODO: Implement retrieval of AddLabelExpense for a transaction
        return null;
    }

    @Override
    public void removeLabelFromAllEntries(int labelId) {
        // TODO: Remove the given labelId from all transactions in storage
    }

    @Override
    public void assignLabelExpense(int id, Label label) {
        // TODO: Assign the given label to the transaction with the given id
    }

    @Override
    public void updateLabel(Label label) {
        // TODO: Update an existing label in LabelData.JSON
    }

    @Override
    public void createLabel(Label label) {
        // TODO: Persist a new label into LabelData.JSON
    }

    @Override
    public Label getLabelById(int labelId) {
        // TODO: Retrieve a label by its ID from LabelData.JSON
        return null;
    }

    @Override
    public boolean userHasLabelName(int userid, String labelName) {
        // TODO: Check if the given user already has a label with this name
        return false;
    }

    @Override
    public void deleteLabel(int labelId) {
        // TODO: Delete the label with the given ID from LabelData.JSON
    }

    // ===============================
    // Helper methods for JSON IO
    // ===============================

    private String readFile(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            return null;
        }
        return sb.toString();
    }

    private void writeFile(File file, String content) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.print(content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file: " + file.getName(), e);
        }
    }

    private JSONObject readBudgetRoot() {
        if (!budgetFile.exists()) {
            return new JSONObject();
        }
        String content = readFile(budgetFile);
        if (content == null) {
            return new JSONObject();
        }
        content = content.trim();
        if (content.isEmpty()) {
            return new JSONObject();
        }

        try {
            return new JSONObject(content);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse BudgetData.JSON", e);
        }
    }

    private void writeBudgetRoot(JSONObject root) {
        writeFile(budgetFile, root.toString(4));
    }
}
