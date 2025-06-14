import java.util.stream.Collectors;

import static java.util.Objects.hash;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public static Task importTask(int id, String name, String description, TaskStatus status) {
        Task task = new Task(name, description, status);
        task.setId(id);
        return task;
    }

    @Override
    public String toString() {
        TaskType type = this instanceof Epic ?
                TaskType.EPIC : this instanceof Subtask ?
                TaskType.SUBTASK : TaskType.TASK;
        String links = null;
        if (this instanceof Epic epic) {
            links = epic.getSubTasks().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "));
        } else if (this instanceof Subtask subtask) {
            links = String.valueOf(subtask.getEpicId());
        }
        return id + "," + type + "," + name + "," + status + ","  + description + "," + links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
       return hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
