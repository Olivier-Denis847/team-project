package Label;

import entity.Label;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LabelTest {

    @Test
    public void testLabelEntity() {
        // 1. Test Constructor and Getters
        Label label = new Label(1, "Food", "Red", "Lunch");

        Assertions.assertEquals(1, label.getLabelId());
        Assertions.assertEquals("Food", label.getLabelName());
        Assertions.assertEquals("Red", label.getColor());
        Assertions.assertEquals("Lunch", label.getDescription());

        // 2. Test Setters
        label.setLabelId(2);
        label.setLabelName("Groceries");
        label.setColor("Blue");
        label.setDescription("Weekly Buy");

        Assertions.assertEquals(2, label.getLabelId());
        Assertions.assertEquals("Groceries", label.getLabelName());
        Assertions.assertEquals("Blue", label.getColor());
        Assertions.assertEquals("Weekly Buy", label.getDescription());

        // 3. Test toString (Essential for coverage if you have an @Override toString)
        String stringResult = label.toString();
        Assertions.assertEquals("Groceries", stringResult);
    }
}