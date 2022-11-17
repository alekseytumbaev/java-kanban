package task_managers;

import task_managers.history_managers.HistoryManager;
import task_managers.history_managers.InMemoryHistoryManager;

public class Managers {
    public static  TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getHistoryDefault() {
        return new InMemoryHistoryManager();
    }
}
