import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int id = 0;
    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        historyManager = Managers.getDefaultHistory();
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>((t1, t2) -> t1.getStartTime().compareTo(t2.getStartTime()));
    }

    @Override
    public int getCurrentId() {
        return id;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
            updateEpicTimeMetrics(epic.getId());
        }
    }

    @Override
    public Task getTaskById(int id) {
        var task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            throw new NotFoundException("Task с заданным id не найден");
        }
    }

    @Override
    public Epic getEpicById(int id) {
        var epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            throw new NotFoundException("Task с заданным id не найден");
        }
    }

    @Override
    public Subtask getSubTaskById(int id) {
        var subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        } else {
            throw new NotFoundException("Task с заданным id не найден");
        }
    }

    @Override
    public int addTask(Task task) {
        int newId = generateId(); // Сделал для удобства использования возврат этого id из метода
        task.setId(newId);
        tasks.put(task.getId(), task);
        return newId;
    }

    @Override
    public int addEpic(Epic epic) {
        int newId = generateId(); // Сделал для удобства использования возврат этого id из метода
        epic.setId(newId);
        epics.put(epic.getId(), epic);
        updateEpicStatus(newId);
        return newId;
    }

    @Override
    public int addSubTask(Subtask subtask) {
        int newId = generateId(); // Сделал для удобства использования возврат этого id из метода
        subtask.setId(newId);
        subtasks.put(subtask.getId(), subtask);
        int relatedEpicId = subtask.getEpicId();
        Epic relatedEpic = epics.get(relatedEpicId);
        if (relatedEpic != null) {
            relatedEpic.addSubTask(newId);
            updateEpicStatus(relatedEpicId);
            updateEpicTimeMetrics(relatedEpicId);
        }
        return newId;
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        var relatedEpicId = subtask.getEpicId();
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(relatedEpicId);
        updateEpicTimeMetrics(relatedEpicId);
    }

    @Override
    public void removeTaskById(int id) {
        tasks.remove(id);
    }

    @Override
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

    @Override
    public void removeSubtaskById(int id) {
        var subtask = subtasks.get(id);
        if (subtask != null) {
            var relatedEpicId = subtask.getEpicId();
            var relatedEpic = epics.get(relatedEpicId);
            subtasks.remove(id);
            relatedEpic.removeSubtaskById(id);
            updateEpicStatus(relatedEpicId);
            updateEpicTimeMetrics(relatedEpicId);
        }
    }

    @Override
    public ArrayList<Integer> getEpicSubtasks(int epicId) {
        var epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubTasks();
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        List<Task> prioritized = new ArrayList<>(tasks.values());
        prioritized.addAll(subtasks.values());
        prioritized.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return t1.getStartTime().compareTo(t2.getStartTime());
            }
        });
        return prioritized;
    }

    private void updateEpicTimeMetrics(int epicId) {
        var epic = epics.get(epicId);
        if (epic != null) {
            var epicSubtasksIds = getEpicSubtasks(epicId);
            LocalDateTime start = epicSubtasksIds.stream()
                    .map(subtasks::get)
                    .map(Subtask::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);
            LocalDateTime end = epicSubtasksIds.stream()
                    .map(subtasks::get)
                    .map(Subtask::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            epic.setStartTime(start);
            epic.setEndTime(end);
            if ((start != null) && (end != null)) {
                epic.setDuration(Duration.between(start, end));
            }
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
                    switch (subtask.getStatus()) {
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
}
