public class Managers {
    public TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
