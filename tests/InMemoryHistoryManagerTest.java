import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private final TaskManager tm = Managers.getDefault();

    @BeforeEach
    public void initTm() {
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
        tm.getSubTaskById(14);
        tm.getSubTaskById(15);
        tm.getSubTaskById(16);
        tm.getEpicById(12);
        tm.getTaskById(5);
    }

    @Test
    public void shouldDeleteOldHistoryRecords() {
        assertEquals(2, tm.getHistory().getFirst().getId(), "Из истории не удалился старый элемент");
    }
}