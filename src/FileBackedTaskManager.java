import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path path;

    public FileBackedTaskManager(String path) {
        this.path = Paths.get(path);
        load();
    }

    Task fromString(String value) {
        String[] items = value.split(",");
        var id = Integer.parseInt(items[0]);
        var type = TaskType.valueOf(items[1]);
        var name = items[2];
        var status = TaskStatus.valueOf(items[3]);
        var description = items[4];
        Task t = Task.importTask(id, name, description, status);
        switch (type) {
            case EPIC : {
                String[] subtasks = items[5].split(" ");
                List<Integer> subtaskIds = new ArrayList<>(subtasks.length);
                for (String subtask : subtasks) {
                    subtaskIds.add(Integer.parseInt(subtask));
                }
                return Epic.importEpicFromTask(t, subtaskIds);
            }
            case SUBTASK : {
                int epicId = Integer.parseInt(items[5]);
                return Subtask.importSubtask(t, epicId);
            }
            case TASK : {
                return t;
            }
        }
        return null;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubTask(Subtask subtask) {
        int id = super.addSubTask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(Subtask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    private int importTask(Task task) {
        tasks.put(task.getId(), task);
        return task.getId();
    }

    private int importEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    private int importSubTask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        return subtask.getId();
    }

    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,links").append("\n");
        for (var task : tasks.values()) {
            sb.append(task).append("\n");
        }
        for (Subtask subtask : subtasks.values()) {
            sb.append(subtask).append("\n");
        }
        for (Epic epic : epics.values()) {
            sb.append(epic).append("\n");
        }
        try {
            Files.writeString(path, sb.toString());
        } catch (IOException e) {
            System.out.println("Ошибка сохранения в файл");
        }
    }

    private void load() {
        if (Files.exists(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toString()))) {
                String line;
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    if (counter != 0) {
                        Task t = fromString(line);
                        if (id < t.getId()) {
                            id = t.getId();
                        }
                        if (t instanceof Epic) {
                            importEpic((Epic) t);
                        } else if (t instanceof Subtask) {
                            importSubTask((Subtask) t);
                        } else {
                            importTask(t);
                        }
                    }
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
