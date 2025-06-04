import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

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