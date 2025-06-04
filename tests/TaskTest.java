import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskTest {

    @Test
    public void testTaskEquals() {
        var task1 = new Task("task1", "test task1", TaskStatus.NEW);
        task1.setId(1);
        var task2 = new Task("task2", "test task2", TaskStatus.NEW);
        task2.setId(2);
        assertNotEquals(task1, task2);
        task2.setId(1);
        assertEquals(task1, task2);
    }
}