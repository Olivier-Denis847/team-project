package data_access;

import entity.Budget;
import entity.Label;
import entity.Transaction;
import use_case.add_transaction.TransactionDataAccessInterface;
import use_case.graph.GraphDataAccessInterface;
import use_case.label.ALEDataAccessInterface;
import use_case.label.AddLabelExpense;
import use_case.label.LabelDataAccessInterface;
import use_case.optimize.OptimizeDataAccessInterface;
import use_case.budget.SetBudgetDataAccessInterface;

import java.util.List;

/**
 * FinanceDataAccess will implement each of the data access interfaces
 * stores to 3 separate JSON files
 * 1: TransactionData.JSON
 *    {
 *        <transaction-id>: {
 *              attributes of each transaction
 *              i.e.
 *              date:
 *              type:
 *        }
 *
 *    }
 * 2: LabelData.JSON
 *    {
 *        <label-id>: {
 *             attributes of each label
 *        }
 *    }
 * 3: BudgetData.JSON
 *    {
 *        <budget-id>: {
 *            attributes of each budget
 *        }
 *    }
 */
public class FinanceDataAccess implements TransactionDataAccessInterface, SetBudgetDataAccessInterface, GraphDataAccessInterface, ALEDataAccessInterface, LabelDataAccessInterface, OptimizeDataAccessInterface {
    /**
     * @param transaction
     */
    @Override
    public void save(Transaction transaction) {

    }

    /**
     * @return
     */
    @Override
    public List<Transaction> getAll() {
        return List.of();
    }

    /**
     * @return
     */
    @Override
    public List<Transaction> getTransactions() {
        return List.of();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public AddLabelExpense getAddLabelExpense(int id) {
        return null;
    }

    /**
     * @param labelId
     */
    @Override
    public void removeLabelFromAllEntries(int labelId) {

    }

    /**
     * @param id
     * @param label
     */
    @Override
    public void assignLabelExpense(int id, Label label) {

    }

    /**
     * @param label
     */
    @Override
    public void updateLabel(Label label) {

    }

    /**
     * @param label
     */
    @Override
    public void createLabel(Label label) {

    }

    /**
     * @param labelId
     * @return
     */
    @Override
    public Label getLabelById(int labelId) {
        return null;
    }

    /**
     * @param userid
     * @param labelName
     * @return
     */
    @Override
    public boolean userHasLabelName(int userid, String labelName) {
        return false;
    }

    /**
     * @param labelId
     */
    @Override
    public void deleteLabel(int labelId) {

    }

    /**
     * Optimizes the expenses for the given parameters.
     *
     * @param expenses the expenses to be optimized
     * @return a text response with helpful advice
     */
    @Override
    public String generateText(String expenses) {
        return "";
    }

    /**
     * get all past entries
     *
     * @return all past entries in an array
     */
    @Override
    public List<Transaction> getAllEntries() {
        return List.of();
    }

    /**
     * save lineGraphRange data for next use
     *
     * @param lineGraphRange the range data for line graph
     */
    @Override
    public void saveGraphRange(String lineGraphRange) {

    }

    /**
     * save the type of entry to graph
     *
     * @param type the type of entry
     */
    @Override
    public void saveGraphType(String type) {

    }

    /**
     * get range data from database
     *
     * @return the graph range data in database, null if file not found
     */
    @Override
    public String getRange() {
        return "";
    }

    /**
     * get type data from database
     *
     * @return the pie graph range data in database, null if file not found
     */
    @Override
    public String getType() {
        return "";
    }

    /**
     * Get only expense transactions from the data source.
     *
     * @return list of expense transactions
     */
    @Override
    public List<Transaction> getExpenses() {
        return List.of();
    }

    /**
     * Get only income transactions from the data source.
     *
     * @return list of income transactions
     */
    @Override
    public List<Transaction> getIncomes() {
        return List.of();
    }

    /**
     * @param month 
     * @return
     */
    @Override
    public Budget getBudgetForMonth(String month) {
        return null;
    }

    /**
     * @param budget 
     */
    @Override
    public void saveBudget(Budget budget) {

    }
}
