package Olivier.use_case;

import Olivier.OptimizeDataAccess;

public class OptimizeInteractor implements OptimizeInputBoundary{
    private final OptimizeDataAccess apiInterface;
    private final OptimizeOutputBoundary outputBoundary;

    public OptimizeInteractor(OptimizeDataAccess apiInterface,
                                OptimizeOutputBoundary outputBoundary) {
        this.apiInterface = apiInterface;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(OptimizeInputData inputData) {
        //ToDo Format the input data from expenses to a prompt

        String prompt = "Give me a cookie recipe";
        final String response = apiInterface.generateText(prompt);
        if (response.equals("failed")) {
            outputBoundary.failureView("error");
        }

        final OptimizeOutputData outputData = new OptimizeOutputData(response);
        outputBoundary.successView(outputData);
    }

    @Override
    public void cancel() {outputBoundary.cancelView();}
}
