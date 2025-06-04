import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final HashMap<Integer, Integer> historyNodesIdentifiers = new HashMap<>();
    private Node head = null;
    private Node tail = null;
    int size = 0;
    int nextIndex = 0;
    private class Node {
        Node prev;
        Node next;
        Task data;

        public Node(Node prev, Node next, Task data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        historyNodesIdentifiers.put(task.getId(), nextIndex++);
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Integer index = historyNodesIdentifiers.get(id);
        if (index != null) {
            removeFromIndexMap(index, id);
            removeByIndex(index);
            nextIndex--;
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        if (head == null) {
            head = new Node(null, null, task);
            tail = head;
        } else {
            Node temp = new Node(tail, null, task);
            tail.next = temp;
            tail = temp;
        }
        size++;
    }

    private void removeByIndex(int index) {
        var currNode = head;
        while (index != 0) {
            currNode = currNode.next;
            index--;
        }
        removeNode(currNode);
        size--;
    }

    private void removeNode(Node node) {
        if (node == head) {
            head = head.next;
            head.prev = null;
        } else if (node == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }

    private void removeFromIndexMap(int startIndex, int id) {
        historyNodesIdentifiers.remove(id);
        for (var entry : historyNodesIdentifiers.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
            if (value > startIndex) {
                historyNodesIdentifiers.put(key, --value);
            }
        }
    }


    private List<Task> getTasks() {
        var result = new ArrayList<Task>(size);
        var node = head;
        while(node != null) {
            result.add(node.data);
            node = node.next;
        }
        return result;
    }
}
