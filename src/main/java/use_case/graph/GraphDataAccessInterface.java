package use_case.graph;

import java.util.List;

import Entity.Transaction;

public interface GraphDataAccessInterface {

    /**
     * get all past entries
     * 
     * @return all past entries in an array
     */
    public List<Transaction> getAllEntries();

    /**
     * save lineGraphRange data for next use
     * 
     * @param lineGraphRange the range data for line graph
     */
    public void saveGraphRange(String lineGraphRange);

    /**
     * save the type of entry to graph
     * 
     * @param type the type of entry
     */
    public void saveGraphType(String type);

    /**
     * get range data from database
     * 
     * @return the graph range data in database, null if file not found
     */
    public String getRange();

    /**
     * get type data from database
     * 
     * @return the pie graph range data in database, null if file not found
     */
    public String getType();

    /**
     * Get only expense transactions from the data source.
     *
     * @return list of expense transactions
     */
    public List<Transaction> getExpenses();

    /**
     * Get only income transactions from the data source.
     *
     * @return list of income transactions
     */
    public List<Transaction> getIncomes();

}
