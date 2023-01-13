package task_managers;

import exception.ManagerSaveException;
import task_managers.history_managers.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public FileBackedTaskManager() {
    }

    protected FileBackedTaskManager(HistoryManager historyManager, Map<Long, Task> tasks, Map<Long, Subtask> subtasks, Map<Long, Epic> epics) {
        super(historyManager, tasks, subtasks, epics);
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        String ls = System.lineSeparator();
        sb.append("id,type,title,status,description,epic").append(ls);

        for (Task task : super.getAllTasks()) {
            sb.append(taskToString(task)).append(ls);
        }
        for (Epic epic : super.getAllEpics()) {
            sb.append(taskToString(epic)).append(ls);
        }
        for (Subtask subtask : super.getAllSubtasks()) {
            sb.append(taskToString(subtask)).append(ls);
        }
        sb.append(ls);
        sb.append(historyToString());

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("tasks.csv"))) {
            bw.write(sb.toString());

        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить данные в файл");
        }
    }

    private String taskToString(Task task) {
        if (task == null) throw new NullPointerException();

        String rez = String.format("%d,%s,%s,%s,%s,%s,%d",
                task.getId(),
                task.getClass().getSimpleName().toUpperCase(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task.getStartTime() == null ? "null" : task.getStartTime(),
                task.getDuration());

        if (task instanceof Subtask)
            rez += String.format(",%d", ((Subtask) task).getEpicId());
        if (task instanceof Epic) {
            rez += String.format(",%s", task.getEndTime() == null ? "null" : task.getEndTime());
        }

        return rez;
    }

    private String historyToString() {
        StringBuilder sb = new StringBuilder();
        List<Task> history = super.getHistory();
        for (Task task : history) {
            sb.append(task.getId()).append(",");
        }
        //удаляем последнюю запятую
        if (!history.isEmpty()) {
            int l = sb.length();
            sb.replace(l - 1, l, "");
        }
        return sb.toString();
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasks = super.getHistory();
        save();
        return tasks;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = super.getAllTasks();
        save();
        return tasks;
    }

    @Override
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public boolean addTask(Task newTask) {
        boolean taskAdded = super.addTask(newTask);
        save();
        return taskAdded;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        boolean taskUpdated = super.updateTask(updatedTask);
        save();
        return taskUpdated;
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        List<Subtask> subtasks = super.getAllSubtasks();
        save();
        return subtasks;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public boolean addSubtask(Subtask newSubtask) {
        boolean subtaskAdded = super.addSubtask(newSubtask);
        save();
        return subtaskAdded;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        boolean subtaskUpdated = super.updateSubtask(updatedSubtask);
        save();
        return subtaskUpdated;
    }

    @Override
    public void deleteSubtaskById(long id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epics = super.getAllEpics();
        save();
        return epics;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        List<Subtask> subtasks = super.getEpicSubtasks(epicId);
        save();
        return subtasks;
    }

    @Override
    public boolean addEpic(Epic newEpic) {
        boolean epicAdded = super.addEpic(newEpic);
        save();
        return epicAdded;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        boolean epicUpdated = super.updateEpic(updatedEpic);
        save();
        return epicUpdated;
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }
}