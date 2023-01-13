package task_managers;

import constant.TaskStatus;
import exception.TasksWithSameStartTimeException;
import task_managers.history_managers.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Instant;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager;
    private long nextId;
    private final Map<Long, Task> tasks;
    private final Map<Long, Subtask> subtasks;
    private final Map<Long, Epic> epics;
    protected final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getHistoryDefault();
        this.tasks = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    protected InMemoryTaskManager(HistoryManager historyManager,
                                  Map<Long, Task> tasks, Map<Long, Subtask> subtasks,
                                  Map<Long, Epic> epics) {
        this.historyManager = historyManager;
        this.tasks = tasks;
        this.subtasks = subtasks;
        this.epics = epics;
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subtasks.values());
    }

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
        addPrioritizedTask(newTask);
        tasks.put(newTask.getId(), newTask);
        return true;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        if (updatedTask == null || !tasks.containsKey(updatedTask.getId())) return false;

        prioritizedTasks.remove(tasks.get(updatedTask.getId()));
        addPrioritizedTask(updatedTask);
        tasks.put(updatedTask.getId(), updatedTask);
        return true;
    }

    @Override
    public void deleteTaskById(long id) {
        prioritizedTasks.remove(tasks.get(id));
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Long taskId : tasks.keySet()) {
            historyManager.remove(taskId);
            prioritizedTasks.remove(tasks.get(taskId));
        }
        tasks.clear();
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
        addPrioritizedTask(newSubtask);
        subtasks.put(newSubtask.getId(), newSubtask);
        return true;
    }

    @Override
    public boolean updateSubtask(Subtask updatedSubtask) {
        if (updatedSubtask == null) return false;

        prioritizedTasks.remove(subtasks.get(updatedSubtask.getId()));
        addPrioritizedTask(updatedSubtask);

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
        if (epic != null)
            epic.getSubtaskIds().remove(id);

        subtasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
        syncEpic(epic);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addPrioritizedTask(Task task) {
        List<Task> prioritizedTasksList = getPrioritizedTasks();

        for (Task curr : prioritizedTasksList) {
            if (curr.getStartTime().equals(task.getStartTime()))
                throw new TasksWithSameStartTimeException("Одинаковое время начала у задач: " + curr + ", " + task);
        }
        prioritizedTasks.add(task);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        }

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
            epic.setStartTime(null);
            epic.setDuration(0);
            epic.setEndTime(null);
            return;
        }

        boolean hasSubtasksNew = false;
        boolean hasSubtasksDone = false;
        boolean notInProgress = true;
        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);

            subtask.setEpicId(epic.getId());

            Instant epicStart = epic.getStartTime();
            Instant subtaskStart = subtask.getStartTime();
            if (epicStart == null || epicStart.isAfter(subtaskStart)) {
                epic.setStartTime(subtask.getStartTime());
            }
            epic.setDuration(epic.getDuration() + subtask.getDuration());

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

        Instant epicEndTime = epic.getStartTime().plusSeconds(epic.getDuration() * 60L);
        epic.setEndTime(epicEndTime);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
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
            prioritizedTasks.remove(subtasks.get(subtaskId));
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
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
        epics.clear();
        subtasks.clear();
    }
    //******************************************************************************************************************
}
