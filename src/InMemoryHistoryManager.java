import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final LinkedList<Task> history = new LinkedList<>();
    private static final HashMap<Integer, Integer> historyNodesIdentifiers = new HashMap<>();
    private int nextId = 0;

    public InMemoryHistoryManager() {

    }

    @Override
    public void add(Task task) {
        Integer oldId = historyNodesIdentifiers.get(task.getId());
        if (oldId != null) {
            remove(oldId);
        }
        historyNodesIdentifiers.put(task.getId(), nextId++);
        history.add(task);
    }

    @Override
    public void remove(int id) {
        int index = historyNodesIdentifiers.get(id);
        history.remove(index);
        historyNodesIdentifiers.remove(id);
        nextId--;
    }

    @Override
    public List<Task> getHistory() {
        var result = new ArrayList<Task>(history.size());
        result.addAll(history);
        return result;
    }
}
