package task_managers;

import task_managers.history_managers.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getHistoryDefault();
    private long nextId;
    private final Map<Long, Task> tasks = new HashMap<>();
    private final Map<Long, Subtask> subtasks = new HashMap<>();
    private final Map<Long, Epic> epics = new HashMap<>();

    private long getNextId() {
        return nextId++;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //********************Tasks*****************************************************************************************
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(long id) {
        if (!tasks.containsKey(id)) return null;

        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public boolean addTask(Task newTask) {
        if (newTask == null) return false;

        newTask.setId(getNextId());
        tasks.put(newTask.getId(), newTask);
        return true;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        if (updatedTask == null || !tasks.containsKey(updatedTask.getId())) return false;

        tasks.put(updatedTask.getId(), updatedTask);
        return true;
    }

    @Override
    public void deleteTaskById(long id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Long taskId : tasks.keySet()) {
            deleteTaskById(taskId);
            historyManager.remove(taskId);
        }
    }
    //******************************************************************************************************************


    //********************Subtasks**************************************************************************************
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public Subtask getSubtaskById(long id) {
        if (!subtasks.containsKey(id)) return null;

        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public boolean addSubtask(Subtask newSubtask) {
        if (newSubtask == null) return false;

        newSubtask.setId(getNextId());
        subtasks.put(newSubtask.getId(), newSubtask);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null) return false;

        Subtask prevSubTask = subtasks.get(updatedSubtask.getId());
        updatedSubtask.setEpicId(prevSubTask.getEpicId());
        subtasks.put(updatedSubtask.getId(), updatedSubtask);
        syncEpic(epics.get(updatedSubtask.getEpicId()));
        return true;
    }

    @Override
    public void deleteSubtaskById(long id) {
        Subtask subtask = getSubtaskById(id);
        if (subtask == null) return;

        Epic epic = getEpicById(subtask.getEpicId());
        if (epic == null) {
            subtasks.remove(id);
            return;
        }

        epic.getSubtaskIds().remove(id);
        subtasks.remove(id);
        historyManager.remove(id);
        syncEpic(epic);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values())
            historyManager.remove(subtask.getId());

        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }
    //******************************************************************************************************************


    //********************Epics*****************************************************************************************
    private void syncEpic(Epic epic) {
        if (epic == null) return;
        if (epic.getSubtaskIds().size() == 0) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean hasSubtasksNew = false;
        boolean hasSubtasksDone = false;
        boolean notInProgress = true;
        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            subtask.setEpicId(epic.getId());

            if (subtask.getStatus() == TaskStatus.NEW)
                hasSubtasksNew = true;
            else if (subtask.getStatus() == TaskStatus.DONE)
                hasSubtasksDone = true;

            if (subtask.getStatus() == TaskStatus.IN_PROGRESS || (hasSubtasksNew && hasSubtasksDone)) {
                epic.setStatus(TaskStatus.IN_PROGRESS);
                notInProgress = false;
            }
        }
        if (notInProgress)
            epic.setStatus(hasSubtasksNew ? TaskStatus.NEW : TaskStatus.DONE);
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public Epic getEpicById(long id) {
        if (!epics.containsKey(id)) return null;

        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(long epicId) {
        Epic epic = getEpicById(epicId);
        if (epic == null) return null;

        List<Subtask> subtasks = new ArrayList<>();
        for (Long subtaskId : epic.getSubtaskIds()) {
            subtasks.add(getSubtaskById(subtaskId));
        }
        return subtasks;
    }

    @Override
    public boolean addEpic(Epic newEpic) {
        if (newEpic == null) return false;

        newEpic.setId(getNextId());
        epics.put(newEpic.getId(), newEpic);
        syncEpic(newEpic);
        return true;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        if (updatedEpic == null || !epics.containsKey(updatedEpic.getId())) return false;

        //Если subtask есть в предыдущем эпике, но его нет в обновленном - удаляем subtask
        Epic prevEpic = epics.get(updatedEpic.getId());
        for (Long subtaskId : prevEpic.getSubtaskIds()) {
            if (!updatedEpic.getSubtaskIds().contains(subtaskId))
                deleteSubtaskById(subtaskId);
        }

        epics.put(updatedEpic.getId(), updatedEpic);
        syncEpic(updatedEpic);
        return true;
    }

    @Override
    public void deleteEpicById(long id) {
        if (!epics.containsKey(id)) return;

        for (Long subtaskId : epics.get(id).getSubtaskIds()) {
            subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values())
            historyManager.remove(epic.getId());
        for (Subtask subtask : subtasks.values())
            historyManager.remove(subtask.getId());
        epics.clear();
        subtasks.clear();
    }
    //******************************************************************************************************************
}
