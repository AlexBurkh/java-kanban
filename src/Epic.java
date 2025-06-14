import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksId = new ArrayList<>();
    }

    public static Epic importEpicFromTask(Task task, List<Integer> subtasks) {
        Epic e = new Epic(task.name, task.description);
        e.setId(task.getId());
        e.setStatus(task.getStatus());
        for (var subtask : subtasks) {
            e.addSubTask(subtask);
        }
        return e;
    }

    public ArrayList<Integer> getSubTasks() {
        return subtasksId;
    }

    public void addSubTask(int id) {
        if (this.id != id) {
            subtasksId.add(id);
        }
    }

    public void removeSubtaskById(int id) {
        subtasksId.removeIf(item -> item == id);
    }

    public void clearSubtasks() {
        subtasksId.clear();
    }
}
