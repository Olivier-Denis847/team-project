package optimize.use_case;

/**
 * DAO interface for the optimize use case
 */
public interface OptimizeDataAccessInterface {
    /**
     * Optimizes the expenses for the given parameters.
     * @param expenses the expenses to be optimized
     * @return a text response with helpful advice
     */
    String generateText(String expenses);
}
