package use_case.optimize;


public class OptimizeInteractor implements OptimizeInputBoundary{
    private final OptimizeDataAccessInterface apiInterface;
    private final OptimizeOutputBoundary outputBoundary;

    public OptimizeInteractor(OptimizeDataAccessInterface apiInterface,
                                OptimizeOutputBoundary outputBoundary) {
        this.apiInterface = apiInterface;
        this.outputBoundary = outputBoundary;
    }

    @Override
    public void execute(OptimizeInputData inputData) {
        StringBuilder output = new StringBuilder()
                .append("Over the next ")
                .append(inputData.getMonths()).append(" months.\n")
                .append("With these priorities for saving:\n");
        for (int i = 0; i < inputData.getLabels().length; i++) {
            output.append(inputData.getLabels()[i])
                    .append(" - ");
            output.append(inputData.getPriorities()[i])
                    .append(" priority,\n");
        }
        String prompt = output.toString();

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
