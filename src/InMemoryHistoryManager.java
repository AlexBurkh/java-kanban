import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final HashMap<Integer, Node> historyNodesIdentifiers = new HashMap<>();
    private Node head = null;
    private Node tail = null;
    int size = 0;

    private class Node {
        Node prev;
        Node next;
        Task data;

        public Node(Node prev, Node next, Task data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public Node getPrev() {
            return this.prev;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getNext() {
            return this.next;
        }
    }

    @Override
    public void add(Task task) {
        remove(task.getId());
        var newNode = new Node(null, null, task);
        historyNodesIdentifiers.put(task.getId(), newNode);
        linkLast(newNode);
    }

    @Override
    public void remove(int id) {
        Node node = historyNodesIdentifiers.get(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Node node) {
        if (tail == null) {
            tail = node;
            head = tail;
        } else {
            node.setPrev(tail);
            tail.next = node;
            tail = node;
        }
        size++;
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
        size--;
    }

    private List<Task> getTasks() {
        var result = new ArrayList<Task>(size);
        var node = head;
        while (node != null) {
            result.add(node.data);
            node = node.next;
        }
        return result;
    }
}
