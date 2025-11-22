package optimize.interface_adapter;

import interface_adapter.optimize.OptimizeState;
import interface_adapter.optimize.OptimizeViewModel;
import org.junit.jupiter.api.Test;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class OptimizeViewModelTest {

    @Test
    public void setStateFiresEvent() {
        OptimizeViewModel vm = new OptimizeViewModel();
        AtomicBoolean called = new AtomicBoolean(false);
        vm.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                called.set(true);
                assertEquals("state", evt.getPropertyName());
                assertNotNull(evt.getNewValue());
            }
        });

        OptimizeState s = new OptimizeState(vm.getState());
        s.setTime(3);
        vm.setState(s);
        assertTrue(called.get(), "PropertyChangeListener should be called on setState");
    }

    @Test
    public void updateTimeAndPrioritiesCreateNewState() {
        OptimizeViewModel vm = new OptimizeViewModel();
        OptimizeState before = vm.getState();
        vm.updateTime(7);
        OptimizeState afterTime = vm.getState();
        assertNotSame(before, afterTime);
        assertEquals(7, afterTime.getTime());

        vm.updatePriorities(new String[] {"Low","High"});
        OptimizeState afterPrior = vm.getState();
        assertNotSame(afterTime, afterPrior);
        assertArrayEquals(new String[] {"Low","High"}, afterPrior.getPriorities());
    }
}
