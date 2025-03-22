import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id = 0;
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public ArrayList<Task> getTasks() {
        return (ArrayList<Task>) tasks.values();
    }

    public ArrayList<Epic> getEpics() {
        return (ArrayList<Epic>) epics.values();
    }

    public ArrayList<Subtask> getSubTasks() {
        return (ArrayList<Subtask>) subtasks.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubTaskById(int id) {
        return subtasks.get(id);
    }

    public int addTask(Task task) {
        int newId = generateId();
        task.setId(newId);
        tasks.put(newId, task);
        return newId;
    }

    public int addEpic(Epic epic) {
        int newId = generateId();
        epic.setId(newId);
        epics.put(newId, epic);
        updateEpicStatus(newId);
        return newId;
    }

    public int addSubTask(Subtask subtask) {
        int newId = generateId();
        subtask.setId(newId);
        subtasks.put(newId, subtask);
        int relatedEpicId = subtask.getEpicId();
        Epic relatedEpic = epics.get(relatedEpicId);
        relatedEpic.addSubTask(newId);
        updateEpicStatus(relatedEpicId);
        return newId;
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubTask(Subtask subtask) {
        var relatedEpicId = subtask.getEpicId();
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(relatedEpicId);
    }

    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    public void removeEpicById(int id) {
        var epic = epics.get(id);
        if (epic != null) {
            var epicSubtasks = getEpicSubtasks(id);
            for (Integer epicSubtask : epicSubtasks) {
                subtasks.remove(epicSubtask);
            }
            epics.remove(id);
        }
    }

    public void removeSubtaskById(int id) {
        var subtask = subtasks.get(id);
        if (subtask != null) {
            var relatedEpicId = subtask.getEpicId();
            var relatedEpic = epics.get(relatedEpicId);
            subtasks.remove(id);
            relatedEpic.removeSubtaskById(id);
            updateEpicStatus(relatedEpicId);
        }
    }

    private ArrayList<Integer> getEpicSubtasks(int epicId) {
        var epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubTasks();
        } else {
            return new ArrayList<>();
        }
    }

    private void updateEpicStatus(int epicId) {
        var epic = epics.get(epicId);
        if (epic != null) {
            var epicSubtasksIds = getEpicSubtasks(epicId);
            if (epicSubtasksIds.isEmpty()) {
                epic.setStatus(TaskStatus.NEW);
                return;
            }
            boolean isAllSubtasksNew = true;
            boolean isAllSubtasksDone = true;
            for (Integer epicSubtaskId : epicSubtasksIds) {
                var subtask = subtasks.get(epicSubtaskId);
                if (subtask != null) {
                    switch(subtask.getStatus()) {
                        case NEW:
                            isAllSubtasksDone = false;
                            break;
                        case DONE:
                            isAllSubtasksNew = false;
                            break;
                        case IN_PROGRESS:
                            isAllSubtasksDone = false;
                            isAllSubtasksNew = false;
                            break;
                    }
                }
            }
            if (isAllSubtasksDone) {
                epic.setStatus(TaskStatus.DONE);
            } else if (isAllSubtasksNew) {
                epic.setStatus(TaskStatus.NEW);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    private int generateId() {
        return ++id;
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "id=" + id +
                ", tasks=" + tasks +
                ", epics=" + epics +
                ", subtasks=" + subtasks +
                '}';
    }
}
