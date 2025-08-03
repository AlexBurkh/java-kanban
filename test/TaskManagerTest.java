import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;


public class TaskManagerTest {
    private final TaskManager tm = Managers.getDefault();

    @Before
    public void initTm() {
        tm.addTask(new Task("task1", "test task1", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 14, 4, 13, 22, 0),
                Duration.ofMinutes(40)));
        tm.addEpic(new Epic("epic2", "test epic1"));
        tm.addTask(new Task("task3", "test task2", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 15, 4, 13, 22, 0),
                Duration.ofMinutes(40)));
        tm.addTask(new Task("task4", "test task3", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 16, 4, 13, 22, 0),
                Duration.ofMinutes(40)));
        tm.addTask(new Task("task5", "test task4", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 17, 4, 13, 22, 0),
                Duration.ofMinutes(40)));
        tm.addTask(new Task("task6", "test task5", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 18, 4, 13, 22, 0),
                Duration.ofMinutes(40)));
        tm.addTask(new Task("task7", "test task6", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 19, 4, 13, 22, 0),
                Duration.ofMinutes(40)));
        tm.addSubTask(new Subtask("subtask8", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 20, 4, 13, 22, 0),
                Duration.ofMinutes(40), 2));
        tm.addSubTask(new Subtask("subtask9", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 21, 4, 13, 22, 0),
                Duration.ofMinutes(40), 2));
        tm.addSubTask(new Subtask("subtask10", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 22, 4, 13, 22, 0),
                Duration.ofMinutes(40), 2));
        tm.addSubTask(new Subtask("subtask11", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 23, 4, 13, 22, 0),
                Duration.ofMinutes(40), 2));
        tm.addEpic(new Epic("epic12", "test epic2"));
        tm.addSubTask(new Subtask("subtask13", "test subtask", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 4, 24, 4, 13, 22, 0),
                Duration.ofMinutes(40), 12));
        tm.addSubTask(new Subtask("subtask14", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 25, 4, 13, 22, 0),
                Duration.ofMinutes(40), 12));
        tm.addSubTask(new Subtask("subtask15", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 26, 4, 13, 22, 0),
                Duration.ofMinutes(40), 12));
        tm.addSubTask(new Subtask("subtask16", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 27, 4, 13, 22, 0),
                Duration.ofMinutes(40), 12));
        tm.addSubTask(new Subtask("subtask17", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 28, 4, 13, 22, 0),
                Duration.ofMinutes(40), 12));
    }

    @Test
    public void shouldNoChangeTaskWhenAddingInTM() {
        var task = new Task("task555", "test task555", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 14, 4, 13, 22, 0),
                Duration.ofMinutes(40));
        var id = tm.addTask(task);
        var taskFromTM = tm.getTaskById(id);
        assertEquals(task.getName(), taskFromTM.getName());
        assertEquals(task.getDescription(), taskFromTM.getDescription());
        assertEquals(task.getStatus(), taskFromTM.getStatus());
    }

    @Test
    public void shouldIncrementCounterWithDiffersTasks() {
        assertEquals(17, tm.getCurrentId());
    }

    @Test
    public void shouldRemoveAllSubtasksOfEpic() {
        tm.removeEpicById(12);
        assertEquals(4, tm.getSubTasks().size());
    }

    @Test
    public void shouldDecreaseTasksSize() {
        tm.removeTaskById(1);
        assertEquals(5, tm.getTasks().size());
    }

    @Test
    public void shouldReturnNoOneSubtaskForEpicWithoutSubtasks() {
        tm.removeSubtaskById(8);
        tm.removeSubtaskById(9);
        tm.removeSubtaskById(10);
        tm.removeSubtaskById(11);
        assertEquals(0, tm.getEpicSubtasks(2).size());
    }

    @Test
    public void shouldChangeSizeToZero() {
        tm.clearEpics();
        tm.clearTasks();
        tm.clearSubtasks();
        assertEquals(0, tm.getTasks().size());
        assertEquals(0, tm.getEpics().size());
        assertEquals(0, tm.getSubTasks().size());
    }
}