import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class TaskManagerTest {
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
    }

    @Test
    public void shouldEqualsTasksInHMAndTM() {
        HistoryManager hm = tm.getHistoryManager();
        var taskFromTM = tm.getTaskById(1);
        var taskFromHM = hm.getHistory().getFirst();
        assertEquals(taskFromTM.getId(), taskFromHM.getId());
        assertEquals(taskFromTM.getStatus(), taskFromHM.getStatus());
        assertEquals(taskFromTM.getName(), taskFromHM.getName());
        assertEquals(taskFromTM.getDescription(), taskFromHM.getDescription());
    }

    @Test
    public void shouldNoChangeTaskWhenAddingInTM() {
        var task = new Task("task555", "test task555", TaskStatus.NEW);
        var id = tm.addTask(task);
        var taskFromTM = tm.getTaskById(id);
        assertEquals(task.getName(), taskFromTM.getName());
        assertEquals(task.getDescription(), taskFromTM.getDescription());
        assertEquals(task.getStatus(), taskFromTM.getStatus());
    }

    @Test
    public void shouldIncrementCounterWithDiffersTasks() {
        assertEquals(17, tm.getCurrentId(), "Некорректно изменяется текущий id задачи");
    }

    @Test
    public void shouldRemoveAllSubtasksOfEpic() {
        tm.removeEpicById(12);
        assertEquals(4, tm.getSubTasks().size());
    }

    @Test
    public void epic2ShouldChangeStatusToDone() {
        tm.removeSubtaskById(11);
        assertEquals(TaskStatus.DONE, tm.getEpicById(2).getStatus());
    }

    @Test
    public void epic2ShouldChangeStatusToNew() {
        tm.removeSubtaskById(8);
        tm.removeSubtaskById(9);
        tm.removeSubtaskById(10);
        tm.removeSubtaskById(11);
        assertEquals(TaskStatus.NEW, tm.getEpicById(2).getStatus());
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
    public void shouldNoChangeCurrentIdIfUpdateTasks() {
        int currentId = tm.getCurrentId();

        //Task
        var task = tm.getTaskById(1);
        int id = task.getId();
        task = new Task("updated task", "updated task with similar id", TaskStatus.IN_PROGRESS);
        task.setId(id);
        tm.updateTask(task);

        // Epic
        var epic = tm.getEpicById(2);
        id = epic.getId();
        var subtasks = epic.getSubTasks();
        epic = new Epic("updated epic", "updated epic with same id");
        for (Integer subtask : subtasks) {
            epic.addSubTask(subtask);
        }
        tm.updateEpic(epic);

        //Subtask
        var subtask = tm.getSubTaskById(10);
        id = subtask.getId();
        int epicId = subtask.getEpicId();
        subtask = new Subtask("updated subtask", "updated subtask with same id", TaskStatus.NEW, epicId);
        tm.updateSubTask(subtask);

        assertEquals(currentId, tm.getCurrentId());
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