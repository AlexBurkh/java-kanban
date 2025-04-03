public class Main {
    public static void main(String[] args) {
        Managers managers = new Managers();

        TaskManager tm = managers.getDefault();

        var taskId = tm.addTask(new Task("домашка", "сделать финальный проект спринта 4",
                TaskStatus.NEW));
        var epic1Id = tm.addEpic(new Epic("курс Java", "пройти курс Java"));
        var subtask1Id = tm.addSubTask(new Subtask("модуль 1", "закончить модуль 1", TaskStatus.DONE,
                epic1Id));
        var subtask2Id = tm.addSubTask(new Subtask("модуль 2", "закончить модуль 2", TaskStatus.DONE,
                epic1Id));
        var subtask3Id = tm.addSubTask(new Subtask("модуль 3", "закончить модуль 3", TaskStatus.DONE,
                epic1Id));
        var subtask4Id = tm.addSubTask(new Subtask("модуль 4", "закончить модуль 4", TaskStatus.NEW,
                epic1Id));
        var epic2Id = tm.addEpic(new Epic("Проект", "Сделать домашний проект"));
        var subtask5Id = tm.addSubTask(new Subtask("БД", "Продумать связь с БД", TaskStatus.DONE,
                epic2Id));
        var subtask6Id = tm.addSubTask(new Subtask("Парсеры", "Доделать парсеры", TaskStatus.NEW,
                epic2Id));
        tm.removeSubtaskById(subtask4Id);
        tm.removeEpicById(epic2Id);
        tm.removeTaskById(taskId);
        System.out.println(tm);
    }
}
