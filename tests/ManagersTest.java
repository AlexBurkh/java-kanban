import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ManagersTest {
    @Test
    public void shouldReturnInitializedHistoryManager() {
        var hm = Managers.getDefaultHistory();
        assertNotNull(hm.getHistory());
    }

    @Test
    public void shouldReturnInitializedTaskManager() {
        var tm = Managers.getDefault();
        assertNotNull(tm.getTasks());
        assertNotNull(tm.getSubTasks());
        assertNotNull(tm.getEpics());
    }
}