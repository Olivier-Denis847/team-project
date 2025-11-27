package interface_adapter.optimize;

import use_case.optimize.OptimizeInputData;
import use_case.optimize.OptimizeInputBoundary;

public class OptimizeController {
    private final OptimizeInputBoundary optimiseInteractor;

    public OptimizeController(OptimizeInputBoundary optimiseInteractor) {
        this.optimiseInteractor = optimiseInteractor;
    }

    /**
     * Executes the optimize use case.
     * @param months the number of months to optimize for
     * @param labels the name of each label in order
     * @param priorities the priority of each label in order
     */
    public void execute(int months, String[] labels, String[] priorities) {
        OptimizeInputData data = new OptimizeInputData(months, labels, priorities);
        optimiseInteractor.execute(data);
    }

    /**
     * Executes the cancel use case.
     */
    public void cancel() {
        optimiseInteractor.cancel();
    }
}
