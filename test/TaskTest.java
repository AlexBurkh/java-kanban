

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class TaskTest {

    @Test
    public void testTaskEquals() {
        var task1 = new Task("task1", "test task1", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 14, 4, 13, 22, 0),
                Duration.ofMinutes(40));
        task1.setId(1);
        var task2 = new Task("task2", "test task2", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 14, 4, 13, 22, 0),
                Duration.ofMinutes(40));
        task2.setId(2);
        assertNotEquals(task1, task2);
        task2.setId(1);
        assertEquals(task1, task2);
    }
}