package data_access;

import entity.Budget;
import entity.Category;
import entity.Label;
import entity.Transaction;
import use_case.add_transaction.TransactionDataAccessInterface;
import use_case.graph.GraphDataAccessInterface;
import use_case.label.ALEDataAccessInterface;
import use_case.label.AddLabelExpense;
import use_case.label.LabelDataAccessInterface;
import use_case.budget.BudgetDataAccessInterface;

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
 * FinanceDataAccess implements the different data access interfaces for this
 * application
 * and persists data into three JSON files:
 *
 * 1) TransactionData.JSON
 * [
 * {
 * "id": 1,
 * "amount": 10.5,
 * "type": "expense",
 * "note": "Coffee",
 * "date": "1732752000000",
 * "labelIds": [1, 2]
 * },
 * ...
 * ]
 *
 * 2) LabelData.JSON
 * (reserved for future label persistence)
 *
 * 3) BudgetData.JSON
 * {
 * "budgets": [
 * {
 * "month": "2025-11",
 * "limit": 1000.0,
 * "totalSpent": 250.0
 * }
 * ],
 * "graphRange": "Month",
 * "graphType": "Expense"
 * }
 */
public class FinanceDataAccess implements
        TransactionDataAccessInterface,
        BudgetDataAccessInterface,
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
        ensureUncategorizedLabel();
    }

    /**
     * Constructor that allows custom file paths (useful for testing).
     */
    public FinanceDataAccess(String transactionPath, String labelPath, String budgetPath) {
        this.transactionFile = new File(transactionPath);
        this.labelFile = new File(labelPath);
        this.budgetFile = new File(budgetPath);
        ensureUncategorizedLabel();
    }

    /**
     * Ensure the default "Uncategorized" label exists for user 1.
     */
    private void ensureUncategorizedLabel() {
        final int DEFAULT_USER_ID = 1;
        if (!labelExists(DEFAULT_USER_ID, "Uncategorized")) {
            Label uncategorized = new Label(0, "Uncategorized", "#CCCCCC",
                    "Default label for uncategorized transactions");
            createLabel(uncategorized);
        }
    }

    /**
     * Get the uncategorized label for default user.
     * 
     * @return the uncategorized Label object
     */
    public Label getUncategorizedLabel() {
        final int DEFAULT_USER_ID = 1;
        List<Label> labels = getAllLabelsByUser(DEFAULT_USER_ID);
        for (Label label : labels) {
            if ("Uncategorized".equalsIgnoreCase(label.getLabelName())) {
                return label;
            }
        }
        // Should not happen since ensureUncategorizedLabel is called in constructor
        return null;
    }

    // ===============================
    // Transaction persistence
    // Credit: Kentaro - originally created TransactionDataAccessObject
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

                // Reconstruct labels from labelIds
                List<Label> labels = new ArrayList<>();
                if (obj.has("labelIds") && !obj.isNull("labelIds")) {
                    JSONArray labelIdsArray = obj.getJSONArray("labelIds");
                    for (int j = 0; j < labelIdsArray.length(); j++) {
                        int labelId = labelIdsArray.getInt(j);
                        Label label = getLabelById(labelId);
                        if (label != null) {
                            labels.add(label);
                        }
                    }
                }

                // for categories instantiation
                Category category = null;
                if (obj.has("category") && !obj.isNull("category")) {
                    String categoryName = obj.optString("category", null);
                    if (categoryName != null) {
                        try {
                            category = Category.valueOf(categoryName.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            category = Category.FOOD; // fallback default
                        }
                    }
                } else {
                    category = Category.FOOD; // default if missing
                }

                // If no labels found, add the Uncategorized label
                if (labels.isEmpty()) {
                    Label uncategorized = getUncategorizedLabel();
                    if (uncategorized != null) {
                        labels.add(uncategorized);
                    }
                }

                Transaction t = new Transaction(id, amount, labels, note, date, type, category);
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
    // BudgetDataAccessInterface
    // Credit: Brandon - originally created FileBudgetDataAccess
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
                float limit = (float) obj.optDouble("limit", 0.0);
                float totalSpent = (float) obj.optDouble("totalSpent", 0.0);
                return new Budget(month, limit, totalSpent);
            }
        }
        return null;
    }

    /**
     * Save or update a budget. If a budget for the same month already exists, it is
     * updated.
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

        boolean updated = false;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String m = obj.optString("month", null);
            if (budget.getMonth().equals(m)) {
                obj.put("month", budget.getMonth());
                obj.put("limit", budget.getLimit());
                obj.put("totalSpent", budget.getTotalSpent());
                updated = true;
                break;
            }
        }

        if (!updated) {
            JSONObject obj = new JSONObject();
            obj.put("month", budget.getMonth());
            obj.put("limit", budget.getLimit());
            obj.put("totalSpent", budget.getTotalSpent());
            arr.put(obj);
        }

        root.put("budgets", arr);
        writeBudgetRoot(root);
    }

    /**
     * Delete a budget for the given month.
     *
     * @param monthKey the month identifier (e.g., "2025-11")
     */
    @Override
    public synchronized void deleteBudget(String monthKey) {
        JSONObject root = readBudgetRoot();
        JSONArray arr = root.optJSONArray("budgets");
        if (arr == null) {
            return;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            String m = obj.optString("month", null);
            if (monthKey.equals(m)) {
                arr.remove(i);
                break;
            }
        }

        root.put("budgets", arr);
        writeBudgetRoot(root);
    }

    // ===============================
    // Label-related interfaces
    // ===============================

    @Override
    public AddLabelExpense getAddLabelExpense(int id) {
        return null;
    }

    @Override
    public void removeLabelFromAllEntries(int labelId) {
        List<Transaction> transactions = getAll();
        boolean modified = false;

        for (Transaction t : transactions) {
            List<Label> labels = t.getLabels();
            if (labels != null) {
                int sizeBefore = labels.size();
                labels.removeIf(l -> l.getLabelId() == labelId);

                if (labels.size() != sizeBefore) {
                    modified = true;

                    // If transaction now has no labels, add Uncategorized
                    if (labels.isEmpty()) {
                        Label uncategorized = getUncategorizedLabel();
                        if (uncategorized != null) {
                            labels.add(uncategorized);
                        }
                    }
                }
            }
        }

        if (modified) {
            writeTransactions(transactions);
        }
    }

    @Override
    public void assignLabelExpense(int id, Label label) {
        List<Transaction> transactions = getAll();
        boolean modified = false;

        for (Transaction t : transactions) {
            if (t.getId() == id) {
                List<Label> labels = t.getLabels();

                // Remove Uncategorized label if this is a new user-added label
                if (labels != null) {
                    labels.removeIf(l -> "Uncategorized".equalsIgnoreCase(l.getLabelName()));

                    // Add the new label if not already present
                    boolean alreadyHasLabel = false;
                    for (Label existingLabel : labels) {
                        if (existingLabel.getLabelId() == label.getLabelId()) {
                            alreadyHasLabel = true;
                            break;
                        }
                    }

                    if (!alreadyHasLabel) {
                        labels.add(label);
                    }
                }

                modified = true;
                break;
            }
        }

        if (modified) {
            writeTransactions(transactions);
        }
    }

    @Override
    public void removeLabelFromExpense(int id, int labelId) {
        List<Transaction> transactions = getAll();
        boolean modified = false;

        for (Transaction t : transactions) {
            if (t.getId() == id) {
                List<Label> labels = t.getLabels();
                if (labels != null) {
                    int before = labels.size();
                    labels.removeIf(l -> l.getLabelId() == labelId);

                    // Check if removal happened before adding Uncategorized
                    if (labels.size() != before) {
                        modified = true;
                    }

                    if (labels.isEmpty()) {
                        Label uncategorized = getUncategorizedLabel();
                        if (uncategorized != null) {
                            labels.add(uncategorized);
                        }
                    }
                }
                break;
            }
        }

        if (modified) {
            writeTransactions(transactions);
        }
    }

    @Override
    public void updateLabel(Label label) {
        JSONObject root = readLabelRoot();
        JSONArray arr = root.optJSONArray("labels");
        if (arr == null) {
            return;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.optInt("labelId") == label.getLabelId()) {
                obj.put("labelName", label.getLabelName());
                obj.put("color", label.getColor());
                obj.put("description", label.getDescription() == null ? "" : label.getDescription());
                break;
            }
        }

        root.put("labels", arr);
        writeLabelRoot(root);
    }

    @Override
    public void createLabel(Label label) {
        JSONObject root = readLabelRoot();
        JSONArray arr = root.optJSONArray("labels");
        if (arr == null) {
            arr = new JSONArray();
        }

        // Assign new labelId if not set
        if (label.getLabelId() == 0) {
            int nextId = 1;
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                int existingId = obj.optInt("labelId", 0);
                if (existingId >= nextId) {
                    nextId = existingId + 1;
                }
            }
            label.setLabelId(nextId);
        }

        JSONObject obj = new JSONObject();
        obj.put("labelId", label.getLabelId());
        obj.put("labelName", label.getLabelName());
        obj.put("color", label.getColor());
        obj.put("description", label.getDescription() == null ? "" : label.getDescription());
        arr.put(obj);

        root.put("labels", arr);
        writeLabelRoot(root);
    }

    @Override
    public Label getLabelById(int labelId) {
        JSONObject root = readLabelRoot();
        JSONArray arr = root.optJSONArray("labels");
        if (arr == null) {
            return null;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.optInt("labelId") == labelId) {
                return new Label(
                        obj.optInt("labelId"),
                        obj.optString("labelName"),
                        obj.optString("color"),
                        obj.optString("description"));
            }
        }
        return null;
    }

    @Override
    public boolean labelExists(int userid, String labelName) {
        JSONObject root = readLabelRoot();
        JSONArray arr = root.optJSONArray("labels");
        if (arr == null) {
            return false;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.optString("labelName").equalsIgnoreCase(labelName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Label> getAllLabelsByUser(int userid) {
        List<Label> result = new ArrayList<>();
        JSONObject root = readLabelRoot();
        JSONArray arr = root.optJSONArray("labels");
        if (arr == null) {
            return result;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            result.add(new Label(
                    obj.optInt("labelId"),
                    obj.optString("labelName"),
                    obj.optString("color"),
                    obj.optString("description")));
        }
        return result;
    }

    @Override
    public void deleteLabel(int labelId) {
        JSONObject root = readLabelRoot();
        JSONArray arr = root.optJSONArray("labels");
        if (arr == null) {
            return;
        }

        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.optInt("labelId") == labelId) {
                arr.remove(i);
                break;
            }
        }

        root.put("labels", arr);
        writeLabelRoot(root);
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

    private JSONObject readLabelRoot() {
        if (!labelFile.exists()) {
            return new JSONObject();
        }
        String content = readFile(labelFile);
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
            throw new RuntimeException("Failed to parse LabelData.JSON", e);
        }
    }

    private void writeLabelRoot(JSONObject root) {
        writeFile(labelFile, root.toString(4));
    }
}
