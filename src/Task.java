import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static java.util.Objects.hash;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;
    protected LocalDateTime startTime;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    protected Duration duration;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public static Task importTask(int id, String name, String description, TaskStatus status,
                                  LocalDateTime startTime, Duration duration) {
        Task task = new Task(name, description, status, startTime, duration);
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
        return id + "," + type + "," + name + "," + status + "," + description + ","
                + (startTime == null ? "null" : startTime)
                + "," + (duration == null ? "null" : duration.toMinutes()) + "," + links;
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
