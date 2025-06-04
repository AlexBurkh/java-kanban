import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.List;

public class InMemoryHistoryManagerTest {
    private final static TaskManager tm = Managers.getDefault();

    @Before
    public void initTM() {
        tm.addTask(new Task("task1", "test task1", TaskStatus.NEW));
        tm.addEpic(new Epic("epic2", "test epic1"));
        tm.addTask(new Task("task3", "test task2", TaskStatus.NEW));
        tm.addTask(new Task("task4", "test task3", TaskStatus.NEW));
        tm.addTask(new Task("task5", "test task4", TaskStatus.NEW));
        tm.addTask(new Task("task6", "test task5", TaskStatus.NEW));
        tm.addTask(new Task("task7", "test task6", TaskStatus.NEW));
        tm.addSubTask(new Subtask("subtask8", "test subtask", TaskStatus.DONE, 2));
        tm.addSubTask(new Subtask("subtask9", "test subtask", TaskStatus.DONE, 2));
        tm.addSubTask(new Subtask("subtask10", "test subtask", TaskStatus.DONE, 2));
        tm.addSubTask(new Subtask("subtask11", "test subtask", TaskStatus.NEW, 2));
        tm.addEpic(new Epic("epic12", "test epic2"));
        tm.addSubTask(new Subtask("subtask13", "test subtask", TaskStatus.IN_PROGRESS, 12));
        tm.addSubTask(new Subtask("subtask14", "test subtask", TaskStatus.NEW, 12));
        tm.addSubTask(new Subtask("subtask15", "test subtask", TaskStatus.NEW, 12));
        tm.addSubTask(new Subtask("subtask16", "test subtask", TaskStatus.NEW, 12));
        tm.addSubTask(new Subtask("subtask17", "test subtask", TaskStatus.NEW, 12));
        tm.getTaskById(1);
        tm.getEpicById(2);
        tm.getTaskById(3);
        tm.getSubTaskById(8);
        tm.getSubTaskById(9);
        tm.getSubTaskById(10);
        tm.getTaskById(1);
        tm.getSubTaskById(15);
        tm.getSubTaskById(16);
        tm.getSubTaskById(17);
        tm.getEpicById(12);
        tm.getTaskById(5);
    }

    @Test
    public void shouldHaveFirstIdEquals2() {
        List<Task> history = tm.getHistory();
        assertEquals(2, history.getFirst().getId());
    }

    @Test
    public void shouldHaveLastIdEquals5() {
        tm.getTaskById(5);
        List<Task> history = tm.getHistory();
        assertEquals(5, history.getLast().getId());
    }

    @Test
    public void shouldHaveHistoryLengthEquals11() {
        List<Task> history = tm.getHistory();
        assertEquals(11, history.size());
    }

    @Test
    public void shouldHaveLastIdEquals15AndSize11() {
        tm.getSubTaskById(15);
        List<Task> history = tm.getHistory();
        assertEquals(15, history.getLast().getId());
        assertEquals(11, history.size());
    }

    @Test
    public void shouldReplaceTailNodeInHistoryForSameTask() {
        tm.getTaskById(5);
        tm.getTaskById(5);
        List<Task> history = tm.getHistory();
        assertEquals(5, history.getLast().getId());
    }
}