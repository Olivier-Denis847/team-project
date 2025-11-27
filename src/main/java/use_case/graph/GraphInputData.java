package use_case.graph;

/*
 * The input data for the graph use case
 */
public class GraphInputData {
    private final String range;
    private final String transactionType;

    public GraphInputData(String graphRange, String transactionType) {
        this.range = graphRange;
        this.transactionType = transactionType;
    }

    /*
     * getter for range
     */
    public String getRange() {
        return this.range;
    }

    /*
     * getter for transaction type
     */
    public String getTransactionType() {
        return this.transactionType;
    }
}