import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private long nextId = 1;
    private Map<Long, Task> tasks = new HashMap<>();

    //********************Tasks**********************************
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(long id) {
        if (id < 0 || !tasks.containsKey(id)) return null;

        return tasks.get(id);
    }

    public boolean createTask(Task newTask) {
        if (newTask == null || tasks.containsKey(newTask.getId())) return false;

        newTask.setId(nextId++);
        tasks.put(newTask.getId(),newTask);
        return true;
    }

    public boolean updateTask(Task updatedTask) {
        if (updatedTask == null || !tasks.containsKey(updatedTask.getId())) return false;

        tasks.put(updatedTask.getId(),updatedTask);
        return true;
    }

    public void deleteTaskById(long id) {
        tasks.remove(id);
    }

    public void deleteAllTasks() {
        tasks.clear();
    }
}
