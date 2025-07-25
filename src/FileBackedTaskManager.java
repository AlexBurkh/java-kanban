import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fbm = new FileBackedTaskManager(file);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int counter = 0;
                while ((line = reader.readLine()) != null) {
                    if (counter != 0) {
                        Task t = fromString(line);
                        if (t != null) {
                            if (fbm.id < t.getId()) {
                                fbm.id = t.getId();
                            }
                            if (t instanceof Epic) {
                                fbm.epics.put(t.getId(), (Epic) t);
                            } else if (t instanceof Subtask) {
                                fbm.subtasks.put(t.getId(), (Subtask) t);
                            } else {
                                fbm.tasks.put(t.getId(), t);
                            }
                        }
                    }
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createFile(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fbm;
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

    private static Task fromString(String value) {
        String[] items = value.split(",");
        var id = Integer.parseInt(items[0]);
        var type = TaskType.valueOf(items[1]);
        var name = items[2];
        var status = TaskStatus.valueOf(items[3]);
        var description = items[4];
        var startTimeString = items[5];
        LocalDateTime startTime = LocalDateTime.parse(startTimeString);
        var durationMinutes = Integer.parseInt(items[6]);
        Duration duration = Duration.ofMinutes(durationMinutes);
        Task t = Task.importTask(id, name, description, status, startTime, duration);
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

    private void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,startTime,duration,links").append("\n");
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
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }
}
