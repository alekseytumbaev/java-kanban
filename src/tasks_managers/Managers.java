package tasks_managers;

public class Managers {
    public static  TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
