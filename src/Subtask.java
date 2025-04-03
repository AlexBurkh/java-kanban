public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        setEpicId(epicId);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        if (this.id != id) {
            epicId = id;
        }
    }

    @Override
    public String toString() {
        return "Subtask{id=" + id + ", title=" + name + ", description=" + description + ", status=" + status
                + ", epicId=" + epicId + "}";
    }
}
