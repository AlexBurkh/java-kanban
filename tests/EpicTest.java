import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void testEpicEquals() {
        var epic1 = new Epic("epic1", "test epic1");
        epic1.setId(1);
        var epic2 = new Epic("epic2", "test epic2");
        epic2.setId(2);
        assertNotEquals(epic1, epic2);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

    @Test
    void shouldNoAddedSubtaskWithIdAsIsItsOwn() {
        var epic = new Epic("epic1", "test epic1");
        epic.setId(1);
        epic.addSubTask(1);
        assertEquals(0, epic.getSubTasks().size());
    }
}