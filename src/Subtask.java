public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        setEpicId(epicId);
    }

    public static Subtask importSubtask(Task task, int epicId) {
        Subtask st = new Subtask(task.getName(), task.getDescription(), task.getStatus(), epicId);
        st.setId(task.getId());
        return st;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        if (this.id != id) {
            epicId = id;
        }
    }
}
