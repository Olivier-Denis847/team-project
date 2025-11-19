package Olivier.use_case;

/**
 * The output boundary for the optimize expenses use case.
 */
public interface OptimizeOutputBoundary {

    /**
     * Prepares the success view for the optimize expenses use case.
     * @param data the output data
     */
    void successView (OptimizeOutputData data);

    /**
     * Prepares the failure view for the optimize expenses use case.
     * @param errorMessage the error message
     */
    void failureView (String errorMessage);

    /**
     * Prepares the home page view.
     */
    void cancelView ();
}
