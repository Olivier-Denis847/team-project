package optimize.use_case;

/**
 * Input boundary for the optimize expenses use case.
 */
public interface OptimizeInputBoundary {
    /**
     * Executes the optimize use case.
     * @param data the input data
     */
    void execute(OptimizeInputData data);

    /**
     * Executes the cancel optimization alternate flow.
     */
    void cancel();
}
