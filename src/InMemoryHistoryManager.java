import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MAX_HISTORY_SIZE = 10;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>(MAX_HISTORY_SIZE);
    }

    @Override
    public void add(Task task) {
        if (history.size() < MAX_HISTORY_SIZE) {
            history.add(task);
        } else {
            history.removeFirst();
            history.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>(MAX_HISTORY_SIZE);
        for (int i = history.size() - 1; i >= 0; i--) {
            result.add(history.get(i));
        }
        return result;
    }
}
