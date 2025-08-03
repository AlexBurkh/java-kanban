import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SubtaskTest {
    @Test
    public void testSubtasksEquals() {
        var epic = new Epic("epic2", "test epic1");
        epic.setId(1);
        var subtask1 = new Subtask("subtask8", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 14, 4, 13, 22, 0),
                Duration.ofMinutes(40), 2);
        subtask1.setId(2);
        var subtask2 = new Subtask("subtask9", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 15, 4, 13, 22, 0),
                Duration.ofMinutes(30), 2);
        subtask2.setId(3);
        assertNotEquals(subtask1, subtask2);
        subtask2.setId(2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    public void shouldNoAddedEpicIdIfItIsAsItsOwn() {
        var epic = new Epic("epic1", "test epic1");
        epic.setId(1);
        var subtask = new Subtask("subtask", "description", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 4, 14, 4, 13, 22, 0),
                Duration.ofMinutes(40), epic.getId());
        subtask.setId(2);
        subtask.setEpicId(2);
        assertEquals(1, subtask.getEpicId());
    }
}