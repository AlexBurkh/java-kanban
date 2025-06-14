import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasksId = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{id=" + id + ", title=" + name + ", description=" + description + ", status=" + status
                + ", subtasksId=" + subtasksId + "}";
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
