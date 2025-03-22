public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int id, TaskStatus status, int epicId) {
        super(name, description, status);
    }

    public int getEpicId() {
        return epicId;
    }
}
