import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    HistoryManager getHistoryManager();
    int getCurrentId();
    List<Task> getHistory();
    ArrayList<Task> getTasks();
    ArrayList<Epic> getEpics();
    ArrayList<Subtask> getSubTasks();
    void clearTasks();
    void clearEpics();
    void clearSubtasks();
    Task getTaskById(int id);
    Epic getEpicById(int id);
    Subtask getSubTaskById(int id);
    int addTask(Task task);
    int addEpic(Epic epic);
    int addSubTask(Subtask subtask);
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubTask(Subtask subtask);
    void removeTaskById(int id);
    void removeEpicById(int id);
    void removeSubtaskById(int id);
    ArrayList<Integer> getEpicSubtasks(int epicId);
}
