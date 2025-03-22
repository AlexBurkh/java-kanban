import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatus status;

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }

    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;

        return id == task.id || Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 17 * result + (description == null ? 0 : description.hashCode());
        result = 43 * result + (status == null ? 0 : status.hashCode());
        result = 7 * result + Objects.hash(id);
        return result;
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
