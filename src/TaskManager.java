import tasks.Subtask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private long nextId = 1;
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();


    //********************Tasks*****************************************************************************************
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public Task getTaskById(long id) {
        if (!tasks.containsKey(id)) return null;

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
    //******************************************************************************************************************


    //********************Subtasks**************************************************************************************
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subtasks.values());
    }

    public Subtask getSubtaskById(long id) {
        if (!subtasks.containsKey(id)) return null;

        return subtasks.get(id);
    }

    public boolean createSubtask(Subtask newSubtask) {
        if (newSubtask == null || subtasks.containsKey(newSubtask.getId())) return false;

        newSubtask.setId(nextId++);
        subtasks.put(newSubtask.getId(),newSubtask);
        return true;
    }

    public boolean updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null || !subtasks.containsKey(updatedSubtask.getId())) return false;

        subtasks.put(updatedSubtask.getId(),updatedSubtask);
        return true;
    }

    public void deleteSubtaskById(long id) {
        subtasks.remove(id);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }
    //******************************************************************************************************************


}
