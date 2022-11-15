package tasks_managers.history_managers;

import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (task == null) return;
        if (history.size() == 10)
            history.remove(0);

        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
