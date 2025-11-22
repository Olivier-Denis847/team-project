package optimize.interface_adapter;

import interface_adapter.optimize.OptimizeState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OptimizeStateTest {

    @Test
    public void copyConstructorCreatesDeepCopy() {
        OptimizeState original = new OptimizeState(null);
        original.setTime(5);
        original.setLabels(new String[] {"A","B"});
        original.setPriorities(new String[] {"Low","High"});
        original.setResult("result");
        original.setError("err");

        OptimizeState copy = new OptimizeState(original);

        // values equal
        assertEquals(original.getTime(), copy.getTime());
        assertArrayEquals(original.getLabels(), copy.getLabels());
        assertArrayEquals(original.getPriorities(), copy.getPriorities());
        assertEquals(original.getResult(), copy.getResult());
        assertEquals(original.getError(), copy.getError());

        // but arrays should not be same reference
        if (original.getLabels() != null && copy.getLabels() != null) {
            assertNotSame(original.getLabels(), copy.getLabels(), "labels array should be cloned");
        }
        if (original.getPriorities() != null && copy.getPriorities() != null) {
            assertNotSame(original.getPriorities(), copy.getPriorities(), "priorities array should be cloned");
        }

        // modifying original arrays should not affect copy
        original.getLabels()[0] = "X";
        assertNotEquals(original.getLabels()[0], copy.getLabels()[0]);
    }

    @Test
    public void defaultConstructorAndToString() {
        OptimizeState s = new OptimizeState(null);
        String str = s.toString();
        assertTrue(str.contains("Time") && str.contains("Labels"));
    }
}
