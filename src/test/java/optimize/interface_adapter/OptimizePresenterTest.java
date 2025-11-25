package optimize.interface_adapter;

import interface_adapter.optimize.OptimizePresenter;
import interface_adapter.optimize.OptimizeState;
import interface_adapter.optimize.OptimizeViewModel;
import use_case.optimize.OptimizeOutputData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OptimizePresenterTest {

    @Test
    public void successViewSetsResult() {
        OptimizeViewModel vm = new OptimizeViewModel();
        OptimizePresenter presenter = new OptimizePresenter(vm);

        OptimizeOutputData data = new OptimizeOutputData("OK message");
        presenter.successView(data);

        OptimizeState s = vm.getState();
        assertEquals("OK message", s.getResult());
    }

    @Test
    public void failureViewSetsError() {
        OptimizeViewModel vm = new OptimizeViewModel();
        OptimizePresenter presenter = new OptimizePresenter(vm);

        presenter.failureView("Oops");
        OptimizeState s = vm.getState();
        assertEquals("Oops", s.getError());
    }
}
