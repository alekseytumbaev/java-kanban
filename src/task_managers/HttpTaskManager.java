package task_managers;

import api.kv.KVTaskClient;
import task_managers.history_managers.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;
import java.util.Map;

public class HttpTaskManager extends FileBackedTaskManager {

    private final KVTaskClient client;
    private final String key;

    protected HttpTaskManager(KVTaskClient client, String key) {
        this.client = client;
        this.key = key;
    }

    protected HttpTaskManager(
            long currentId,
            HistoryManager historyManager,
            Map<Long, Task> tasks, Map<Long, Subtask> subtasks, Map<Long, Epic> epics,
            KVTaskClient client, String key) {
        super(currentId, historyManager, tasks, subtasks, epics);
        this.client = client;
        this.key = key;
    }

    private void put() {
        client.put(key, serialize());
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = super.getHistory();
        put();
        return tasks;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = super.getAllTasks();
        put();
        return tasks;
    }

    @Override
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        put();
        return task;
    }

    @Override
    public boolean addTask(Task newTask) {
        boolean taskAdded = super.addTask(newTask);
        put();
        return taskAdded;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        boolean taskUpdated = super.updateTask(updatedTask);
        put();
        return taskUpdated;
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        put();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        put();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = super.getAllSubtasks();
        put();
        return subtasks;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = super.getSubtaskById(id);
        put();
        return subtask;
    }

    @Override
    public boolean addSubtask(Subtask newSubtask) {
        boolean subtaskAdded = super.addSubtask(newSubtask);
        put();
        return subtaskAdded;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        boolean subtaskUpdated = super.updateSubtask(updatedSubtask);
        put();
        return subtaskUpdated;
    }

    @Override
    public void deleteSubtaskById(long id) {
        super.deleteSubtaskById(id);
        put();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        put();
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epics = super.getAllEpics();
        put();
        return epics;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = super.getEpicById(id);
        put();
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        List<Subtask> subtasks = super.getEpicSubtasks(epicId);
        put();
        return subtasks;
    }

    @Override
    public boolean addEpic(Epic newEpic) {
        boolean epicAdded = super.addEpic(newEpic);
        put();
        return epicAdded;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        boolean epicUpdated = super.updateEpic(updatedEpic);
        put();
        return epicUpdated;
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        put();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        put();
    }
}
