import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class InMemoryHistoryManagerTest {
    private TaskManager tm;

    @BeforeEach
    public void initTM() {
        tm = Managers.getDefault();
        tm.addTask(new Task("task1", "test task1", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 18, 4, 13, 22, 0),
                Duration.ofMinutes(10)));
        tm.addEpic(new Epic("epic2", "test epic1"));
        tm.addTask(new Task("task3", "test task2", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 19, 3, 13, 22, 0),
                Duration.ofMinutes(10)));
        tm.addTask(new Task("task4", "test task3", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 20, 2, 13, 22, 0),
                Duration.ofMinutes(10)));
        tm.addTask(new Task("task5", "test task6", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 23, 23, 13, 22, 0),
                Duration.ofMinutes(10)));
        tm.addSubTask(new Subtask("subtask6", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 24, 22, 13, 22, 0),
                Duration.ofMinutes(10), 2));
        tm.addSubTask(new Subtask("subtask7", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 25, 21, 13, 22, 0),
                Duration.ofMinutes(10), 2));
        tm.addSubTask(new Subtask("subtask8", "test subtask", TaskStatus.DONE,
                LocalDateTime.of(2025, 4, 26, 20, 13, 22, 0),
                Duration.ofMinutes(10), 2));
        tm.addSubTask(new Subtask("subtask9", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 27, 19, 13, 22, 0),
                Duration.ofMinutes(10), 2));
        tm.addEpic(new Epic("epic10", "test epic2"));
        tm.addSubTask(new Subtask("subtask11", "test subtask", TaskStatus.IN_PROGRESS,
                LocalDateTime.of(2025, 4, 28, 18, 13, 22, 0),
                Duration.ofMinutes(10), 10));
        tm.addSubTask(new Subtask("subtask12", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 29, 17, 13, 22, 0),
                Duration.ofMinutes(10), 10));
        tm.addSubTask(new Subtask("subtask13", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 30, 16, 13, 22, 0),
                Duration.ofMinutes(10), 10));
        tm.addSubTask(new Subtask("subtask14", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 1, 15, 13, 22, 0),
                Duration.ofMinutes(10), 10));
        tm.addSubTask(new Subtask("subtask15", "test subtask", TaskStatus.NEW,
                LocalDateTime.of(2025, 4, 2, 14, 13, 22, 0),
                Duration.ofMinutes(10), 10));
    }

    @Test
    public void shouldHaveFirstIdEquals2() {
        try {
            tm.getTaskById(1);
            tm.getEpicById(2);
            tm.getTaskById(3);
            List<Task> history = tm.getHistory();
            assertEquals(1, history.getFirst().getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void shouldHaveLastIdEquals5() {
        tm.getTaskById(3);
        tm.getTaskById(5);
        List<Task> history = tm.getHistory();
        assertEquals(5, history.getLast().getId());
    }

    @Test
    public void shouldHaveHistoryLengthEquals11() {
        try {
            tm.getTaskById(1);
            tm.getEpicById(2);
            tm.getTaskById(3);
            List<Task> history = tm.getHistory();
            assertEquals(3, history.size());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void shouldReplaceTailNodeInHistoryForSameTask() {
        try {
            tm.getTaskById(5);
            tm.getTaskById(5);
            List<Task> history = tm.getHistory();
            assertEquals(5, history.getLast().getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}