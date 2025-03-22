import java.util.ArrayList;

public class Epic extends Task {
    ArrayList<Integer> subTasksId;

    public Epic(String name, String description, int id, TaskStatus status) {
        super(name, description, status);
        subTasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasks() {
        return subTasksId;
    }

    public void addSubTask(int id) {
        subTasksId.add(id);
    }
}
