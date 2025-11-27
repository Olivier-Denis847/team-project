package optimize.use_case;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import use_case.optimize.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OptimizeInteractorTest {

    private OptimizeDataAccessInterface apiInterface;
    private OptimizeOutputBoundary outputBoundary;
    private OptimizeInteractor interactor;

    @BeforeEach
    void setUp() {
        apiInterface = mock(OptimizeDataAccessInterface.class);
        outputBoundary = mock(OptimizeOutputBoundary.class);
        interactor = new OptimizeInteractor(apiInterface, outputBoundary);
    }

    @Test
    void testExecuteSuccess() {
        // Arrange
        OptimizeInputData inputData = new OptimizeInputData(
                3,
                new String[]{"A", "B"},
                new String[]{"High", "Low"}
        );

        when(apiInterface.generateText(anyString()))
                .thenReturn("Generated response");

        // Act
        interactor.execute(inputData);

        // Assert API prompt correctness
        ArgumentCaptor<String> promptCaptor = ArgumentCaptor.forClass(String.class);
        verify(apiInterface).generateText(promptCaptor.capture());
        String promptSent = promptCaptor.getValue();

        assertTrue(promptSent.contains("Over the next 3 months."));
        assertTrue(promptSent.contains("A - High priority"));
        assertTrue(promptSent.contains("B - Low priority"));

        // Assert successView called
        ArgumentCaptor<OptimizeOutputData> dataCaptor =
                ArgumentCaptor.forClass(OptimizeOutputData.class);

        verify(outputBoundary).successView(dataCaptor.capture());
        OptimizeOutputData outputData = dataCaptor.getValue();

        assertEquals("Generated response", outputData.getMessage());
    }

    @Test
    void testExecuteFailedAPIResponseTriggersFailure() {
        // Arrange
        OptimizeInputData inputData = new OptimizeInputData(
                1,
                new String[]{"A"},
                new String[]{"High"}
        );

        when(apiInterface.generateText(anyString()))
                .thenReturn("failed");

        // Act
        interactor.execute(inputData);

        // Assert: failureView should be called
        verify(outputBoundary).failureView("An issue occurred with the API");

    }

    @Test
    void testCancelCallsCancelView() {
        // Act
        interactor.cancel();

        // Assert
        verify(outputBoundary).cancelView();
    }

    @Test
    void testPromptFormattingIsCorrect() {
        OptimizeInputData inputData = new OptimizeInputData(
                2,
                new String[]{"Rent", "Food", "Travel"},
                new String[]{"High", "Medium", "Low"}
        );

        when(apiInterface.generateText(anyString()))
                .thenReturn("ok");

        interactor.execute(inputData);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(apiInterface).generateText(captor.capture());

        String prompt = captor.getValue();

        assertTrue(prompt.contains("Over the next 2 months."));
        assertTrue(prompt.contains("Rent - High priority"));
        assertTrue(prompt.contains("Food - Medium priority"));
        assertTrue(prompt.contains("Travel - Low priority"));
    }
}
